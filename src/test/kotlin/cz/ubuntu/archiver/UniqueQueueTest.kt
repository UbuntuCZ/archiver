package cz.ubuntu.archiver

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class UniqueQueueTest {

    @Test
    fun keepsOrder() {
        // Arrange
        val q = UniqueQueue<String>()

        // Act
        listOf("3", "5", "1").forEach { q.offer(it) }

        // Assert
        assertThat(q.hasNext()).isTrue()
        assertThat(q.poll()).isEqualTo("3")
        assertThat(q.poll()).isEqualTo("5")
        assertThat(q.poll()).isEqualTo("1")
        assertThat(q.size()).isZero()
    }

    @Test
    fun isUnique() {
        // Arrange
        val q = UniqueQueue<String>()

        // Act
        listOf("3", "5", "5", "3", "3", "1").forEach { q.offer(it) }

        // Assert
        assertThat(q.hasNext()).isTrue()
        assertThat(q.poll()).isEqualTo("3")
        assertThat(q.poll()).isEqualTo("5")
        assertThat(q.poll()).isEqualTo("1")
        assertThat(q.size()).isZero()
    }

    @Test
    fun returnsNullIfEmpty() {
        // Arrange
        val q = UniqueQueue<String>()

        // Assert
        assertThat(q.hasNext()).isFalse()
        assertThat(q.size()).isZero()
        assertThat(q.poll()).isNull()
    }

}
