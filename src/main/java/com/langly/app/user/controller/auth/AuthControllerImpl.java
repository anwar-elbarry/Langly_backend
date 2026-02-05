package com.langly.app.user.controller.auth;

import com.langly.app.user.service.auth.AuthService;
import com.langly.app.user.web.dto.auth.AuthRequest;
import com.langly.app.user.web.dto.auth.AuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Authentication", description = "Gestion de l'authentification")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthControllerImpl  implements AuthController{
    @Operation(summary = "Connexion utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentification réussie"),
            @ApiResponse(responseCode = "401", description = "Identifiants invalides")
    })
    @PostMapping("/login")
    @Override
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request){
        return ResponseEntity.ok(this.authService.login(request));
    }

        private final AuthService authService;

        @Operation(summary = "Déconnexion utilisateur")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "204", description = "Déconnexion réussie"),
                @ApiResponse(responseCode = "401", description = "Token invalide")
        })
        @PostMapping("/logout")
        @Override
        public ResponseEntity<Void> logout(HttpServletRequest request){
               this.authService.logout(request);
               return ResponseEntity.noContent().build();
        }

        @Operation(summary = "Rafraîchir le token")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Token rafraîchi avec succès"),
                @ApiResponse(responseCode = "401", description = "Token invalide ou expiré")
        })
        @PostMapping("/refreshToken")
        @Override
        public ResponseEntity<Map<String,String>> refreshToken(@RequestBody String token){
           return ResponseEntity.ok(this.authService.refreshToken(token));
        }
}
