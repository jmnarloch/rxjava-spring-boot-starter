package io.iceflower.spring.boot.rxjava.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

/**
 * A simple DTO used for testing purpose.
 *
 * @author Jakub Narloch
 */
public class EventDto {

  private final String name;

  private final Date date;

  @JsonCreator
  public EventDto(@JsonProperty("name") String name, @JsonProperty("date") Date date) {
    this.name = name;
    this.date = date;
  }

  public String getName() {
    return name;
  }

  public Date getDate() {
    return date;
  }
}
