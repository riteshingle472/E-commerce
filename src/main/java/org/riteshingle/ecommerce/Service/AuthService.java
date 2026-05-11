package org.riteshingle.ecommerce.Service;

import lombok.RequiredArgsConstructor;
import org.riteshingle.ecommerce.DTO.LoginDTO;
import org.riteshingle.ecommerce.DTO.ResetPasswordDTO;
import org.riteshingle.ecommerce.DTO.SignUpDTO;
import org.riteshingle.ecommerce.Entity.Role;
import org.riteshingle.ecommerce.Entity.UserEntity;
import org.riteshingle.ecommerce.Repository.RoleRepository;
import org.riteshingle.ecommerce.Repository.UserRepository;
import org.riteshingle.ecommerce.Utils.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;


//    Sign-up user
    public SignUpDTO signup(SignUpDTO signUpDTO){

        if(!userRepository.existsByEmail(signUpDTO.getEmail())){
            UserEntity newUser = toEntity(signUpDTO);
            Role role = roleRepository.findById(1L).orElseThrow(() -> new RuntimeException("Role Not Found"));
            String activationToken = UUID.randomUUID().toString();
            if(newUser.getRole() == null){
                newUser.setRole(new HashSet<>());
            }
            newUser.setActivationToken(activationToken);
            newUser.getRole().add(role);
            userRepository.save(newUser);

            String activationLink = "http://localhost:8080/auth/activate?activationToken="+activationToken;
            String subject = "Activate your ecommerce account";
            String body = "Click on the following link to activate you account : "+activationLink;

            emailService.sendMail(newUser.getEmail(), subject,body);
            return toDTO(newUser);
        }else throw new RuntimeException("User already exist...");
    }

    public Map<String,Object> login(LoginDTO dto){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getEmail(),dto.getPassword()));

        try {
            String token = jwtUtils.generateToken(dto.getEmail());
            return Map.of(
                    "TOKEN",token,
                    "USER",toDTO(getPublicProfile(dto.getEmail()))
            );
        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new RuntimeException("Invalid Credentials");
        }
    }

    public String removeUser(Long id){
        UserEntity user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
        return "User Removed ...";
    }

    public UserEntity getCurrentProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert  authentication != null;
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UserEntity getPublicProfile(String email){
        UserEntity user = null;
        if (email == null) user = getCurrentProfile();
        else user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return user;
    }

    public Boolean isActive(String email){
        return userRepository.findByEmail(email).map(UserEntity::getIsActive).orElse(false);
    }

    public Boolean activateUser(String activationToken){
        return userRepository.findByActivationToken(activationToken).map(profile -> {
            profile.setIsActive(true);
            userRepository.save(profile);
            return true;
        }).orElse(false);
    }

    public String sendOtp(String email){
        Boolean hasExist = userRepository.existsByEmail(email);

        if(hasExist){
            UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found with " + email));
            SecureRandom random = new SecureRandom();
            String otp = String.valueOf(100000  + random.nextInt(900000));
            LocalDateTime expireAt = LocalDateTime.now().plusMinutes(10);
            user.setResetOtpExpiredAt(expireAt);
            user.setResetOtp(otp);
            userRepository.save(user);

            String subject = "Your OTP for Password Reset";
            String body = "Dear User,\n\n" +
                    "Your OTP for password reset is: " + otp + "\n" +
                    "This OTP is valid for 10 minutes.\n\n" +
                    "If you did not request this, please ignore this email.\n\n" +
                    "Thanks,\nE-commerce Team";

            try {
                emailService.sendMail(email,subject,body);
            }catch (Exception e){
                System.out.println(e.getMessage());
                throw new RuntimeException(e.getMessage());
            }
            return "OTP send successfully ! ..";
        }else{
            throw new RuntimeException("User not found with : "+email);
        }
    }

    public String changePassword(ResetPasswordDTO resetPasswordDTO){
        UserEntity user = userRepository.findByEmail(resetPasswordDTO.getEmail()).orElseThrow(() -> new RuntimeException("User not found with : " + resetPasswordDTO.getEmail()));

        if(!resetPasswordDTO.getOtp().equalsIgnoreCase(user.getResetOtp())){
            throw new RuntimeException("Invalid OTP");
        }

        if (user.getResetOtpExpiredAt().isBefore(LocalDateTime.now())){
            throw new RuntimeException("OTP Expired");
        }

        user.setNewPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
        userRepository.save(user);

        user.setResetOtp(null);
        user.setResetOtpExpiredAt(null);

        return "Password changed";
    }



    private UserEntity toEntity(SignUpDTO dto){
        return UserEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .contactNo(dto.getContactNo())
                .address(dto.getAddress())
                .pinCode(dto.getPinCode())
                .newPassword(passwordEncoder.encode(dto.getNewPassword()))
                .isActive(dto.getIsActive())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }

    private SignUpDTO toDTO(UserEntity user){
        return SignUpDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .contactNo(user.getContactNo())
                .address(user.getAddress())
                .pinCode(user.getPinCode())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
