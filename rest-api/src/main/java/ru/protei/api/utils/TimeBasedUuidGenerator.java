package ru.protei.api.utils;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import java.util.UUID;

public class TimeBasedUuidGenerator {

  private static EthernetAddress ethernetAddress;
  private static TimeBasedGenerator generator;

  static {
    ethernetAddress = EthernetAddress.fromInterface();
    generator = Generators.timeBasedGenerator(ethernetAddress);
  }

  public static UUID generate() {
    return generator.generate();
  }

  public static TimeBasedGenerator createGenerator() {
    return Generators.timeBasedGenerator(ethernetAddress);
  }
}
