import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.*;

import java.util.*;

public class PrioritySchedulingExample {

    public static void main(String[] args) {

        try {
            // Step 1: Initialize CloudSim
            int num_user = 1;
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false;

            CloudSim.init(num_user, calendar, trace_flag);

            // Step 2: Create Datacenter
            Datacenter datacenter0 = createDatacenter("Datacenter_0");

            // Step 3: Create Broker
            DatacenterBroker broker = new DatacenterBroker("Broker");
            int brokerId = broker.getId();

            // Step 4: Create VM
            List<Vm> vmList = new ArrayList<>();

            int vmid = 0;
            int mips = 1000;
            long size = 10000;
            int ram = 512;
            long bw = 1000;
            int pesNumber = 1;
            String vmm = "Xen";

            Vm vm = new Vm(vmid, brokerId, mips, pesNumber, ram, bw, size, vmm,
                    new CloudletSchedulerTimeShared());
            vmList.add(vm);

            broker.submitVmList(vmList);

            // Step 5: Create Cloudlets with Priority
            List<MyCloudlet> cloudletList = new ArrayList<>();

            UtilizationModel utilizationModel = new UtilizationModelFull();

            cloudletList.add(new MyCloudlet(0, 4000, 2, brokerId, utilizationModel));
            cloudletList.add(new MyCloudlet(1, 2000, 1, brokerId, utilizationModel));
            cloudletList.add(new MyCloudlet(2, 3000, 3, brokerId, utilizationModel));

            // Step 6: Apply Priority Scheduling (SORTING)
            Collections.sort(cloudletList, new Comparator<MyCloudlet>() {
                public int compare(MyCloudlet c1, MyCloudlet c2) {
                    return Integer.compare(c1.getPriority(), c2.getPriority());
                }
            });

            // Convert to normal Cloudlet list
            List<Cloudlet> finalList = new ArrayList<>(cloudletList);

            broker.submitCloudletList(finalList);

            // Step 7: Start Simulation
            CloudSim.startSimulation();

            List<Cloudlet> resultList = broker.getCloudletReceivedList();

            CloudSim.stopSimulation();

            // Step 8: Print Results
            printCloudletList(resultList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Datacenter Creation
    private static Datacenter createDatacenter(String name) throws Exception {

        List<Host> hostList = new ArrayList<>();

        List<Pe> peList = new ArrayList<>();
        peList.add(new Pe(0, new PeProvisionerSimple(1000)));

        int hostId = 0;
        int ram = 2048;
        long storage = 1000000;
        int bw = 10000;

        hostList.add(
                new Host(
                        hostId,
                        new RamProvisionerSimple(ram),
                        new BwProvisionerSimple(bw),
                        storage,
                        peList,
                        new VmSchedulerTimeShared(peList)
                )
        );

        String arch = "x86";
        String os = "Linux";
        String vmm = "Xen";
        double time_zone = 10.0;
        double cost = 3.0;

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, cost, 0.05, 0.1, 0.1);

        return new Datacenter(name, characteristics,
                new VmAllocationPolicySimple(hostList), new LinkedList<>(), 0);
    }

    // Output Method
    private static void printCloudletList(List<Cloudlet> list) {
        System.out.println("\n===== OUTPUT =====");

        for (Cloudlet cloudlet : list) {
            System.out.println("Cloudlet ID: " + cloudlet.getCloudletId()
                    + " | VM ID: " + cloudlet.getVmId()
                    + " | Execution Time: " + cloudlet.getActualCPUTime());
        }
    }
}

// Custom Cloudlet with Priority
class MyCloudlet extends Cloudlet {
    private int priority;

    public MyCloudlet(int id, long length, int priority, int brokerId,
                      UtilizationModel utilizationModel) {

        super(id, length, 1, 300, 300,
                utilizationModel, utilizationModel, utilizationModel);

        this.priority = priority;
        setUserId(brokerId);
    }

    public int getPriority() {
        return priority;
    }
}