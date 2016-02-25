package com.aipai.adw.misc;

import java.net.*;
import java.util.Enumeration;

/**
 * Created by zhangwusheng on 15/11/9.
 */
public class IPAddressTest {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        try{
            Enumeration<NetworkInterface> interfaceList= NetworkInterface.getNetworkInterfaces ( );
            if(interfaceList==null){
                System.out.println("--No interface found--");
            }
            else{
                while(interfaceList.hasMoreElements()){
                    System.out.println ("--------------------" );
                    NetworkInterface iface=interfaceList.nextElement();
                    System.out.println("Interface "+iface.getName()+":");
                    Enumeration<InetAddress > addrList=iface.getInetAddresses();
                    if(!addrList.hasMoreElements()){
                        System.out.println("\t(No address for this address)");
                    }
                    while(addrList.hasMoreElements()){
                        System.out.println ("================" );
                        InetAddress address=addrList.nextElement();


                        System.out.print("\tAddress "+((address instanceof InetAddress? "v4"
                                :(address instanceof Inet6Address ? "(v6)":"(?)"))));
                        System.out.println(":"+address.getHostAddress());

                        System.out.println ("isLoopbackAddress:"+address.isLoopbackAddress () );
                        System.out.println ("isAnyLocalAddress:"+address.isAnyLocalAddress ( ) );
                        System.out.println ("isLinkLocalAddress:"+address.isLinkLocalAddress ( ) );
                        System.out.println ("isSiteLocalAddress:"+address.isSiteLocalAddress ( ) );

                    }
                }
            }
        }
        catch(SocketException e){
            System.out.println("Error getting network interfaces:"+e.getMessage());
            e.printStackTrace();
        }

        for(String host:args){
            try{
                System.out.println(host+":");
                InetAddress[] addressList=InetAddress.getAllByName(host);
                for(InetAddress address:addressList){
                    System.out.println("\t"+address.getHostName()+"/"+address.getHostAddress());
                }
            }
            catch(UnknownHostException e){
                System.out.println("\tUnable to find address for "+host);
                e.printStackTrace();
            }
        }
    }

}
