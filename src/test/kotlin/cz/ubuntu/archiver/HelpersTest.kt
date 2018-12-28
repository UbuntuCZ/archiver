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
