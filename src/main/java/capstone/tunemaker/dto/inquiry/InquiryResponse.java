package capstone.tunemaker.dto.inquiry;

import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InquiryResponse {

    private String title;

    private String contents;

    private Boolean reply;

    private LocalDateTime addTime;
}
