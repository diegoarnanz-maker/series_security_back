package series_back.restcontroller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import series_back.modelo.dto.LoginDto;
import series_back.modelo.dto.UserRequestDto;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthRestcontrollerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Login Exitoso
    @Test
    public void login_Success() throws Exception {
        LoginDto loginDto = new LoginDto("admin", "12345678");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login exitoso"))
                .andExpect(jsonPath("$.user").value("admin"));
    }

    // Login con Credenciales Incorrectas
    @Test
    public void login_Failure() throws Exception {
        LoginDto loginDto = new LoginDto("admin", "wrongpassword");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Credenciales incorrectas"));
    }

    // @Test
    // public void register_Success() throws Exception {
    //     UserRequestDto userDto = new UserRequestDto("nuevoUsuario", "nuevo@email.com", "12345678");

    //     mockMvc.perform(post("/auth/register")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(objectMapper.writeValueAsString(userDto)))
    //             .andExpect(status().isCreated())
    //             .andExpect(jsonPath("$.message").value("Usuario registrado exitosamente"))
    //             .andExpect(jsonPath("$.username").value("nuevoUsuario"))
    //             .andExpect(jsonPath("$.email").value("nuevo@email.com"));
    // }

}
