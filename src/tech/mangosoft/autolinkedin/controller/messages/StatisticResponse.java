package tech.mangosoft.autolinkedin.controller.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatisticResponse {

    @JsonProperty
    private String assignmentName;

    @JsonProperty
    private String status;

    @JsonProperty
    private String errorMessage;

    @JsonProperty
    private long processed;

    @JsonProperty
    private long saved;

    @JsonProperty
    private long successed;

    @JsonProperty
    private long failed;

    @JsonProperty
    private int page;
}
