package org.riteshingle.ecommerce.Controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.riteshingle.ecommerce.DTO.LoginDTO;
import org.riteshingle.ecommerce.DTO.ResetPasswordDTO;
import org.riteshingle.ecommerce.DTO.SignUpDTO;
import org.riteshingle.ecommerce.Service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-up")
    public ResponseEntity<SignUpDTO> signup(@RequestBody SignUpDTO signUpDTO){
        SignUpDTO signup = authService.signup(signUpDTO);
        return ResponseEntity.ok(signup);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String ,Object>> login(@RequestBody LoginDTO loginDTO){
        if(!authService.isActive(loginDTO.getEmail())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body( Map.of(
                    "message","User is not verified"
            ));
        }
        Map<String, Object> response = authService.login(loginDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/activate")
    public ResponseEntity<String > activateUser(@RequestParam String activationToken){
        Boolean isActive = authService.activateUser(activationToken);
        if(isActive) return ResponseEntity.ok("Account activated successfully");
        else return ResponseEntity.status(HttpStatus.NOT_FOUND).body(activationToken);
    }

    @GetMapping("/remove-user")
    public String removeUser(@RequestParam Long id){
        return authService.removeUser(id);
    }


    @GetMapping("/test")
    public String test(){
        return "Test case";
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/reset-password")
    public ResponseEntity<String > sendOTP(@RequestParam String email){
        return ResponseEntity.ok(authService.sendOtp(email));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/change-password")
    public ResponseEntity<String > changePassword(@RequestBody ResetPasswordDTO resetPasswordDTO){
        return ResponseEntity.ok(authService.changePassword(resetPasswordDTO));
    }
}
