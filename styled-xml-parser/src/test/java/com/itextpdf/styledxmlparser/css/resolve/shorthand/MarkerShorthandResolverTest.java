package com.itextpdf.styledxmlparser.css.resolve.shorthand;

import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("UnitTest")
public class MarkerShorthandResolverTest extends ExtendedITextTest {
    @Test
    public void initialValueTest() {
        String shorthandExpression = "initial";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "marker-start: initial",
                "marker-mid: initial",
                "marker-end: initial"
        ));
        IShorthandResolver markerResolver =
                ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.MARKER);
        assertNotNull(markerResolver);
        List<CssDeclaration> resolvedShorthandProps = markerResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY))
    public void emptyValueTest() {
        String shorthandExpression = " ";
        Set<String> expectedResolvedProperties = new HashSet<>();
        IShorthandResolver markerResolver =
                ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.MARKER);
        assertNotNull(markerResolver);
        List<CssDeclaration> resolvedShorthandProps = markerResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void invalidValueTest() {
        String shorthandExpression = "junk";
        Set<String> expectedResolvedProperties = new HashSet<>();
        IShorthandResolver markerResolver =
                ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.MARKER);
        assertNotNull(markerResolver);
        List<CssDeclaration> resolvedShorthandProps = markerResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void invalidUrlTest() {
        String shorthandExpression = "url(test";
        Set<String> expectedResolvedProperties = new HashSet<>();
        IShorthandResolver markerResolver =
                ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.MARKER);
        assertNotNull(markerResolver);
        List<CssDeclaration> resolvedShorthandProps = markerResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void validTest() {
        String shorthandExpression = "url(markers.svg#arrow)";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "marker-start: url(markers.svg#arrow)",
                "marker-mid: url(markers.svg#arrow)",
                "marker-end: url(markers.svg#arrow)"
        ));
        IShorthandResolver markerResolver =
                ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.MARKER);
        assertNotNull(markerResolver);
        List<CssDeclaration> resolvedShorthandProps = markerResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }
}
