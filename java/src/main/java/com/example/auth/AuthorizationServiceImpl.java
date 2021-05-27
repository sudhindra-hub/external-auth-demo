package com.example.auth;

import com.google.rpc.Code;
import com.google.rpc.Status;
import io.envoyproxy.envoy.config.core.v3.HeaderValue;
import io.envoyproxy.envoy.config.core.v3.HeaderValueOption;
import io.envoyproxy.envoy.service.auth.v3.*;

import io.envoyproxy.envoy.type.v3.HttpStatus;
import io.envoyproxy.envoy.type.v3.StatusCode;
import io.grpc.stub.StreamObserver;

import java.io.ByteArrayInputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

public class AuthorizationServiceImpl extends AuthorizationGrpc.AuthorizationImplBase {

    private static X509Certificate getCertificate(final String certificateString) throws CertificateException {
        X509Certificate certificate = null;

        if (certificateString != null && !certificateString.trim().isEmpty()) {
            String certificateStringTmp = certificateString.replace("-----BEGIN CERTIFICATE-----\n", "")
                    .replace("-----END CERTIFICATE-----", "");
            byte[] certificateData = Base64.getDecoder().decode(certificateStringTmp);
            CertificateFactory cf = CertificateFactory.getInstance("X509");
            certificate = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(certificateData));
        }

        return certificate;
    }

    @Override
    public void check(CheckRequest request,
                      StreamObserver<CheckResponse> responseObserver) {

        try {
            String decodedCertStr = URLDecoder.decode(request.getAttributes().getSource().getCertificate(), StandardCharsets.UTF_8.toString());
            System.out.println("Certificate => \n" + decodedCertStr);

            X509Certificate certificate = AuthorizationServiceImpl.getCertificate(Base64.getEncoder().encodeToString(decodedCertStr.getBytes()));
            System.out.println("Subject => " + certificate.getSubjectDN().getName());

            if (certificate.getSubjectDN().getName().contains("client1")) {
                System.out.println("Request for client1");

                OkHttpResponse okHttpResponse = OkHttpResponse.newBuilder().addHeaders(HeaderValueOption.newBuilder().setHeader(HeaderValue.newBuilder().setKey("foo").setValue("bar").build())).build();
                CheckResponse response = CheckResponse.newBuilder().setOkResponse(okHttpResponse).build();

                responseObserver.onNext(response);
                responseObserver.onCompleted();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        DeniedHttpResponse deniedHttpResponse = DeniedHttpResponse.newBuilder().setStatus(HttpStatus.newBuilder().setCode(StatusCode.Unauthorized).build()).build();
        CheckResponse response = CheckResponse.newBuilder().setDeniedResponse(deniedHttpResponse).setStatus(Status.newBuilder().setCode(Code.PERMISSION_DENIED.getNumber()).build()).build();;

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}