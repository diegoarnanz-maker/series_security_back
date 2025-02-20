package series_back.restcontroller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import series_back.modelo.dto.SerieDto;
import series_back.modelo.entities.Serie;
import series_back.modelo.services.ISerieService;

@SpringBootTest
@AutoConfigureMockMvc
public class SerieRestcontrollerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ISerieService serieService;

    @Test
    public void getAllSeries_ReturnsListOfSeries() throws Exception {
        List<SerieDto> seriesList = Arrays.asList(
                new SerieDto(1L, "Breaking Bad", "Un profesor de química...", "Drama", 2008, 9.5, "breakingbad.jpg",
                        "https://youtu.be/HhesaQXLuRY"),
                new SerieDto(2L, "Stranger Things", "Un grupo de niños...", "Ciencia Ficción", 2016, 8.7,
                        "strangerthings.jpg", "https://youtu.be/b9EkMc79ZSU"));

        when(serieService.findAllWithDto()).thenReturn(seriesList);

        mockMvc.perform(get("/api/series")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].title").value("Breaking Bad"))
                .andExpect(jsonPath("$[1].title").value("Stranger Things"));
    }


}
