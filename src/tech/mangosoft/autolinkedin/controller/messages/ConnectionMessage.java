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
public class ConnectionMessage {

    @JsonProperty
    private String login;

    @JsonProperty
    private String location;

    @JsonProperty
    private String fullLocationString;

    @JsonProperty
    private String position;

    @JsonProperty
    private String industries;

    @JsonProperty
    private String message;
}
