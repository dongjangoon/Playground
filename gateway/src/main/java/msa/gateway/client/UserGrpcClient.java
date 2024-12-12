package msa.gateway.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import msa.user.service.UserServiceGrpc;
import msa.user.service.UserOuterClass.GetUserRequest;
import msa.user.service.UserOuterClass.GetUserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserGrpcClient {

    private final UserServiceGrpc.UserServiceBlockingStub userServiceStub;

    public UserGrpcClient() {
        // gRPC 채널 설정 (User Service의 주소와 포트를 변경해야함), Mocking 해서 테스트하고 User 수정할 예정
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext() // TLS 비활성화 (개발 환경)
                .build();

        // gRPC Stub 생성
        this.userServiceStub = UserServiceGrpc.newBlockingStub(channel);
    }

    public GetUserResponse getUser(Long userId) {
        // 요청 메시지 생성
        GetUserRequest request = GetUserRequest.newBuilder()
                .setId(userId)
                .build();

        // gRPC 호출
        return userServiceStub.getUser(request);
    }
}
