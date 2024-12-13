package msa.gateway.mock;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import msa.user.service.UserOuterClass.GetUserRequest;
import msa.user.service.UserOuterClass.GetUserResponse;
import msa.user.service.UserServiceGrpc;

import java.io.IOException;

public class MockUserGrpcServer {

    private final Server server;

    public MockUserGrpcServer(int port) {
        this.server = ServerBuilder.forPort(port)
                .addService(new MockUserService())
                .build();
    }

    public void start() throws IOException {
        server.start();
        System.out.println("Mock gRPC Server started on port " + server.getPort());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("Shutting down Mock gRPC Server...");
            MockUserGrpcServer.this.stop();
        }));
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    static class MockUserService extends UserServiceGrpc.UserServiceImplBase {
        @Override
        public void getUser(GetUserRequest request, StreamObserver<GetUserResponse> responseObserver) {
            // Mock 데이터 생성
            GetUserResponse response = GetUserResponse.newBuilder()
                    .setId(request.getId())
                    .setEmail("mockuser@example.com")
                    .setNickname("MockUser")
                    .setProfileImage("https://example.com/profile.png")
                    .setRole(common.UserRoleOuterClass.UserRole.USER)
                    .setStatus(common.UserStatusOuterClass.UserStatus.ACTIVE)
                    .setCreatedAt("2023-01-01T00:00:00Z")
                    .setUpdatedAt("2023-01-02T00:00:00Z")
                    .setDeleted(false)
                    .build();

            // 응답 전송
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        MockUserGrpcServer server = new MockUserGrpcServer(9090); // 클라이언트와 동일한 포트 설정
        server.start();
        server.blockUntilShutdown();
    }
}
