package cz.ubuntu.archiver

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class IsLinkToDomainTest {

    @Test
    fun keepsTheRightDomain() {
        // Arrange
        val url = "https://example.com/path/to/file.txt?query=string#fragment"

        // Act
        val result = Helpers.isLinkToDomain("example.com")(url)

        // Assert
        assertThat(result).isTrue()
    }

    @Test
    fun filtersDifferentDomain() {
        // Arrange
        val url = "https://another.example.com/path/to/file.txt?query=string#fragment"

        // Act
        val result = Helpers.isLinkToDomain("example.com")(url)

        // Assert
        assertThat(result).isFalse()
    }

}

class IsUrlToSkipTest {

    @Test
    fun skipsUserLoginAndRegister() {
        // Arrange
        val url1 = "https://example.com/user/login"
        val url2 = "https://example.com/user/register?foo=bar"

        // Act + Assert
        assertThat(Helpers.isUrlToSkip(url1)).isTrue()
        assertThat(Helpers.isUrlToSkip(url2)).isTrue()
    }

    @Test
    fun skipsDashExportAtTheStart() {
        // Arrange
        val url = "https://example.com/_export/some/file"

        // Act + Assert
        assertThat(Helpers.isUrlToSkip(url)).isTrue()
    }

    @Test
    fun doesNotSkipDashExportInTheMiddle() {
        // Arrange
        val url = "https://example.com/nested/_export/some/file"

        // Act + Assert
        assertThat(Helpers.isUrlToSkip(url)).isFalse()
    }

    @Test
    fun skipsDoInQueryString() {
        // Arrange
        val url1 = "https://example.com/path/to/file.txt?do=foo"
        val url2 = "https://example.com/path/to/file.txt?dontDo=foo&do=bar"

        // Act + Assert
        assertThat(Helpers.isUrlToSkip(url1)).isTrue()
        assertThat(Helpers.isUrlToSkip(url2)).isTrue()
    }

    @Test
    fun doesNotSkipNotDoInQueryString() {
        // Arrange
        val url = "https://example.com/path/to/file.txt?notdo=do"

        // Act + Assert
        assertThat(Helpers.isUrlToSkip(url)).isFalse()
    }

    @Test
    fun skipsActionProfile() {
        // Arrange
        val url = "https://example.com/path/to/file.txt?action=profile;u=0"

        // Act + Assert
        assertThat(Helpers.isUrlToSkip(url)).isTrue()
    }
}

class RemoveFragmentTest {

    @Test
    fun removesFragment() {
        // Arrange
        val url = "https://example.com/path/to/file.txt?query=string#fragment"

        // Act
        val result = Helpers.removeFragment(url)

        // Assert
        assertThat(result).isEqualTo("https://example.com/path/to/file.txt?query=string")
    }

}

class RemoveQueryStringKeyTest {

    @Test
    fun removesKey() {
        // Arrange
        val url = "https://example.com/path/to/file.txt?query=string&key=value#fragment"

        // Act
        val result = Helpers.removeQueryStringKey("key")(url)

        // Assert
        assertThat(result).isEqualTo("https://example.com/path/to/file.txt?query=string#fragment")
    }

    @Test
    fun removesOnlyKey() {
        // Arrange
        val url = "https://example.com/path/to/file.txt?key=value#fragment"

        // Act
        val result = Helpers.removeQueryStringKey("key")(url)

        // Assert
        assertThat(result).isEqualTo("https://example.com/path/to/file.txt#fragment")
    }
}
