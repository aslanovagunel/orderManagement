package com.app.yolla.modules.auth.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.yolla.modules.auth.dto.AuthResponse;
import com.app.yolla.modules.auth.dto.LoginRequest;
import com.app.yolla.modules.auth.dto.OtpVerificationRequest;
import com.app.yolla.modules.auth.service.AuthService;
import com.app.yolla.shared.dto.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * Authentication Controller Sinfi
 * <p>
 * Bu sinif giriş və qeydiyyat əlaqəli bütün HTTP sorğularını idarə edir.
 * OTP göndərmə, doğrulama və token yaratma əməliyyatlarını təmin edir.
 * <p>
 * Analogi: Bu sinif bir "qeydiyyat masası" kimidir - gələnləri qarşılayır,
 * sənədlərini yoxlayır və sistemi girişi təmin edir.
 */
@Tag(name = "Auth", description = "İstifadəçi identifikasiyası və token əməliyyatları")
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

	@Operation(summary = "OTP göndər", description = "İstifadəçinin telefon nömrəsinə OTP kodu göndərir. İstifadəçi mövcuddursa giriş, yoxdursa qeydiyyat üçün istifadə olunur.")
    @PostMapping("/send-otp")
	public ResponseEntity<ApiResponse<Map<String, Object>>> sendOtp(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        logger.info("OTP göndərmə sorğusu: telefon={}", request.getPhoneNumber());

        try {
			String ipAddress = getClientIpAddress(httpRequest);

			Map<String, Object> sendOtp = authService.sendOtp(request.getPhoneNumber(), ipAddress);

			ApiResponse<Map<String, Object>> response = ApiResponse.success("OTP kodu telefon nömrənizə göndərildi",
					sendOtp);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("OTP göndərmə xətası: telefon={}", request.getPhoneNumber(), e);

			ApiResponse<Map<String, Object>> response = ApiResponse.error("OTP göndərilmədi: " + e.getMessage());

			return ResponseEntity.badRequest().body(response);
        }
    }

	@Operation(summary = "OTP doğrula və daxil ol", description = "İstifadəçinin göndərilən OTP kodunu doğrulayır və JWT token yaradır.")
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOtp(
            @Valid @RequestBody OtpVerificationRequest request) {

        logger.info("OTP doğrulama sorğusu: telefon={}", request.getPhoneNumber());

		try {
            AuthResponse authResponse = authService.verifyOtpAndLogin(
                    request.getPhoneNumber(),
					request.getOtpCode());

            ApiResponse<AuthResponse> response = ApiResponse.success(
					"Uğurla giriş etdiniz", authResponse);

            return ResponseEntity.ok(response);

		} catch (Exception e) {
			logger.error("OTP doğrulama xətası: telefon={}", request.getPhoneNumber(), e);

			ApiResponse<AuthResponse> response = ApiResponse.error("Giriş uğursuz: " + e.getMessage());

			return ResponseEntity.badRequest().body(response);
		}
    }

	@Operation(summary = "Token yenilə", description = "Refresh token ilə yeni access token yaradır.")
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @RequestHeader("Authorization") String refreshToken) {

        logger.info("Token yeniləmə sorğusu");

        try {
            if (refreshToken.startsWith("Bearer ")) {
                refreshToken = refreshToken.substring(7);
            }

            AuthResponse authResponse = authService.refreshToken(refreshToken);

            ApiResponse<AuthResponse> response = ApiResponse.success(
                    "Token uğurla yeniləndi",
					authResponse);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Token yeniləmə xətası: ", e);

            ApiResponse<AuthResponse> response = ApiResponse.error(
					"Token yenilənmədi: " + e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

	@Operation(summary = "Çıxış et", description = "İstifadəçini sistemdən çıxarır və token-ı blacklist edir.")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            @RequestHeader("Authorization") String token) {

        logger.info("Çıxış sorğusu");

        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            authService.logout(token);

            ApiResponse<String> response = ApiResponse.success(
                    "Uğurla çıxış etdiniz",
					null);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Çıxış xətası: ", e);

            ApiResponse<String> response = ApiResponse.error(
					"Çıxış zamanı xəta baş verdi: " + e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

	@Operation(summary = "Token etibarlılığını yoxla", description = "Verilmiş token-in etibarlı olub-olmadığını yoxlayır.")
    @GetMapping("/validate-token")
    public ResponseEntity<ApiResponse<Boolean>> validateToken(
            @RequestHeader("Authorization") String token) {

        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            boolean isValid = authService.validateToken(token);

            ApiResponse<Boolean> response = ApiResponse.success(
                    isValid ? "Token etibarlıdır" : "Token etibarsızdır",
					isValid);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Token doğrulama xətası: ", e);

            ApiResponse<Boolean> response = ApiResponse.error(
					"Token doğrulanmadı: " + e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

	@Operation(summary = "Cari istifadəçinin profilini əldə et", description = "Authorization token-ı vasitəsilə cari istifadəçinin profil məlumatlarını qaytarır.")
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<Object>> getCurrentUserProfile(
            @RequestHeader("Authorization") String token) {

        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            Object userProfile = authService.getCurrentUserProfile(token);

            ApiResponse<Object> response = ApiResponse.success(
                    "Profil məlumatları",
					userProfile);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Profil məlumatları əldə edilmədi: ", e);

            ApiResponse<Object> response = ApiResponse.error(
					"Profil məlumatları əldə edilmədi: " + e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

	@Operation(summary = "Sistem sağlamlıq yoxlaması", description = "Authentication modulunun işləyib-işləmədiyini yoxlayır.")
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        logger.debug("Auth health check sorğusu");

        ApiResponse<String> response = ApiResponse.success(
                "Authentication sistemi işləyir",
				"OK");

        return ResponseEntity.ok(response);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.getRemoteAddr();
        } else {
            return xForwardedForHeader.split(",")[0].trim();
        }
    }
}
