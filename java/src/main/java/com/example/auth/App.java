package com.example.auth;

import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;

import java.io.FileInputStream;

public class App
{
    public static void main( String[] args ) throws Exception
    {
        Server server = NettyServerBuilder.forPort(8080)
                .useTransportSecurity(new FileInputStream("/etc/certs/tls.crt"), new FileInputStream("/etc/certs/tls.key"))
                .addService(new AuthorizationServiceImpl())
                .build();

        server.start();
        System.out.println("Server started");
        server.awaitTermination();
    }
}
