package com.dave.grpc.jwt.springsecurity.service;

import com.google.protobuf.Empty;
import com.grpc.solution.Employee;
import com.grpc.solution.EmployeeServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class EmployeeGrpcService extends EmployeeServiceGrpc.EmployeeServiceImplBase {

    @Override
    public void getEmployeeInfo(Empty request, StreamObserver<Employee> responseObserver) {
        //super.getEmployeeInfo(request, responseObserver);
        responseObserver.onNext(Employee.newBuilder().setName("Solutions").setSalary(123).build());
        responseObserver.onCompleted();
    }
}
