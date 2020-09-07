package org.triplea.generic.xml.scanner;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAndIs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.triplea.generic.xml.TestUtils;

class XmlScannerTest {

  private InputStream inputStream;
  private XmlScanner xmlScanner;

  @BeforeEach
  void setup() throws Exception {
    inputStream = TestUtils.openFile("scanner-example.xml");
    xmlScanner = new XmlScanner(inputStream);
  }

  @AfterEach
  void tearDown() throws Exception {
    inputStream.close();
  }

  @Test
  void scanningForMapName() {
    final String result =
        xmlScanner
            .scanForAttributeValue(
                AttributeScannerParameters.builder().tag("info").attributeName("name").build())
            .orElseThrow();

    assertThat(result, is("World At War"));
  }

  @ParameterizedTest
  @MethodSource
  void scanningForMapNameNegativeCases(final AttributeScannerParameters parameters) {
    final Optional<String> result = xmlScanner.scanForAttributeValue(parameters);

    assertThat(result, isEmpty());
  }

  @SuppressWarnings("unused")
  static List<AttributeScannerParameters> scanningForMapNameNegativeCases() {
    return List.of(
        AttributeScannerParameters.builder().tag("DNE").attributeName("name").build(),
        AttributeScannerParameters.builder().tag("info").attributeName("DNE").build());
  }

  @Test
  void scanningForTagBodyText() {
    final Optional<String> result =
        xmlScanner.scanForBodyText(
            BodyTextScannerParameters.builder()
                .parentTag("property")
                .parentTagAttributeName("name")
                .parentTagAttributeValue("notes")
                .childTag("value")
                .build());

    assertThat(result, isPresentAndIs("These are the game notes!"));
  }

  @ParameterizedTest
  @MethodSource
  void scanningForTagBodyTextNegativeCases(final BodyTextScannerParameters parameters) {
    final Optional<String> result = xmlScanner.scanForBodyText(parameters);

    assertThat(result, is(isEmpty()));
  }

  @SuppressWarnings("unused")
  static List<BodyTextScannerParameters> scanningForTagBodyTextNegativeCases() {
    return List.of(
        BodyTextScannerParameters.builder()
            .parentTag("DNE")
            .parentTagAttributeName("name")
            .parentTagAttributeValue("notes")
            .childTag("value")
            .build(),
        BodyTextScannerParameters.builder()
            .parentTag("property")
            .parentTagAttributeName("DNE")
            .parentTagAttributeValue("notes")
            .childTag("value")
            .build(),
        BodyTextScannerParameters.builder()
            .parentTag("property")
            .parentTagAttributeName("name")
            .parentTagAttributeValue("DNE")
            .childTag("value")
            .build(),
        BodyTextScannerParameters.builder()
            .parentTag("property")
            .parentTagAttributeName("name")
            .parentTagAttributeValue("notes")
            .childTag("DNE")
            .build());
  }
}
