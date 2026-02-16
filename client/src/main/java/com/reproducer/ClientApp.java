package com.reproducer;

import java.util.ArrayList;
import java.util.Collections;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.lang.IgniteRunnable;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.jetbrains.annotations.NotNull;

public class ClientApp {

    public static void main(String[] args) {
        System.out.println("Starting client node...");

        IgniteConfiguration cfg = getClientConfiguration();

        try (Ignite client = Ignition.start(cfg)) {
            System.out.println("Client node started successfully: " + client.cluster().localNode().id());

            client.compute().run(new GetCacheTask());
        } catch (Exception e) {
            System.err.println("Client error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static class GetCacheTask implements IgniteRunnable {

        @IgniteInstanceResource
        private transient Ignite ignite;

        @Override
        public void run() {
            try {
                System.out.println("=== Starting cache operations on server side ===");

                int key = 1;

                // Тест 1: Прямое кэширование POJO
                System.out.println("\n--- Test 1: Caching single MyPojo object ---");
                IgniteCache<Integer, Object> plainCache = ignite.getOrCreateCache("myPlainCache");
                MyPojo myPojo = new MyPojo("test value");

                System.out.println("Putting: " + myPojo);
                plainCache.put(key, myPojo);
                System.out.println("Successfully cached single MyPojo");

                Object plainResult = plainCache.get(key);
                System.out.println("Retrieved: " + plainResult);
                System.out.println("Class: " + plainResult.getClass().getName());
                System.out.println("Test 1: SUCCESS");

                // Тест 2: Кэширование ArrayList с MyPojo
                System.out.println("\n--- Test 2: Caching ArrayList with MyPojo ---");
                IgniteCache<Integer, Object> listCache = ignite.getOrCreateCache("myListCache");

                Object[] listValue = new Object[3];
                listValue[0] = 42L;
                listValue[1] = "string value";
                listValue[2] = myPojo;

                System.out.println("Putting: " + listValue);
                listCache.put(key, listValue);
                System.out.println("Successfully cached ArrayList");

                Object listResult = listCache.get(key);
                System.out.println("Retrieved: " + listResult);
                System.out.println("Class: " + listResult.getClass().getName());
                System.out.println("Test 2: SUCCESS");

                System.out.println("\n=== All cache operations completed ===");

            } catch (Exception e) {
                System.err.println("ERROR in cache operation: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static @NotNull IgniteConfiguration getClientConfiguration() {
        IgniteConfiguration cfg = new IgniteConfiguration();

        cfg.setClientMode(true);
        cfg.setPeerClassLoadingEnabled(true);
        cfg.setIgniteInstanceName("client");

        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
        ipFinder.setAddresses(Collections.singletonList("127.0.0.1:47500..47509"));
        cfg.setDiscoverySpi(new TcpDiscoverySpi().setIpFinder(ipFinder));

        return cfg;
    }
}

