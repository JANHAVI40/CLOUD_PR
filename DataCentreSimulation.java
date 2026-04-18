import java.util.*; 
import org.cloudbus.cloudsim.*; 
import org.cloudbus.cloudsim.core.CloudSim; 
import org.cloudbus.cloudsim.provisioners.*; 
 
public class DataCentreSimulation { 
    public static void main(String[] args) { 
        try { 
            CloudSim.init(1, Calendar.getInstance(), false); 
 
            // Host 
            List<Pe> peList = new ArrayList<Pe>(); 
            peList.add(new Pe(0, new PeProvisionerSimple(1000))); 
 
            Host host = new Host( 
                    0, 
                    new RamProvisionerSimple(2048), 
                    new BwProvisionerSimple(10000), 
                    1000000, 
                    peList, 
                    new VmSchedulerTimeShared(peList) 
            ); 
 
            List<Host> hostList = new ArrayList<Host>(); 
            hostList.add(host); 
 
            // Datacenter 
            DatacenterCharacteristics characteristics = 
                    new DatacenterCharacteristics( 
                            "x86", "Linux", "Xen", 
                            hostList, 10.0, 3.0, 0.05, 0.001, 0.0 
                    ); 
 
            Datacenter datacenter = new Datacenter( 
                    "Datacenter_0", 
                    characteristics, 
                    new VmAllocationPolicySimple(hostList), 
                    new LinkedList<Storage>(), 
                    0 
            ); 
 
            // Broker 
            DatacenterBroker broker = new DatacenterBroker("Broker"); 
            int brokerId = broker.getId(); 
 
            // VM 
            Vm vm = new Vm( 
                    0, brokerId, 1000, 1, 512, 1000, 
                    10000, "Xen", 
                    new CloudletSchedulerTimeShared() 
            ); 
 
            List<Vm> vmList = new ArrayList<Vm>(); 
            vmList.add(vm); 
            broker.submitVmList(vmList); 
 
            // Cloudlet 
            Cloudlet cloudlet = new Cloudlet( 
                    0, 4000, 1, 300, 300, 
                    new UtilizationModelFull(), 
                    new UtilizationModelFull(), 
                    new UtilizationModelFull() 
            ); 
 
            cloudlet.setUserId(brokerId); 
 
            List<Cloudlet> cloudletList = new ArrayList<Cloudlet>(); 
            cloudletList.add(cloudlet); 
            broker.submitCloudletList(cloudletList); 
 
            // Simulation 
            CloudSim.startSimulation(); 
            CloudSim.stopSimulation(); 
 
            // Result 
            List<Cloudlet> resultList = broker.getCloudletReceivedList(); 
            Cloudlet result = resultList.get(0); 
 
            System.out.println("Cloudlet ID: " + result.getCloudletId()); 
            System.out.println("Status: SUCCESS"); 
            System.out.println("Start Time: " + result.getExecStartTime()); 
            System.out.println("Finish Time: " + result.getFinishTime()); 
 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
    } 
}