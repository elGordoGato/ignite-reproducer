package com.reproducer;

import java.util.Collections;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;


public class ServerNode {

    public static void main(String[] args) {
        System.out.println("Starting server node...");

        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setIgniteInstanceName("server");
        cfg.setPeerClassLoadingEnabled(true);

        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
        ipFinder.setAddresses(Collections.singletonList("127.0.0.1:47500..47509"));
        cfg.setDiscoverySpi(new TcpDiscoverySpi().setIpFinder(ipFinder));

        Ignite server = Ignition.start(cfg);
        System.out.println("Server node started successfully: " + server.cluster().localNode().id());

        try {
            Class.forName("com.reproducer.example.IgniteDeserializationReproducer$MyPojo");
            System.out.println("WARNING: MyPojo class found on server!");
        } catch (ClassNotFoundException e) {
            System.out.println("MyPojo class NOT found on server (as expected)");
        }
    }
}