package cz.ubuntu.archiver

class UniqueQueue<E>(c: Collection<E> = emptyList()) {

    private val linkedSet: LinkedHashSet<E> = LinkedHashSet(c)

    fun offer(e: E): Boolean = linkedSet.add(e)

    fun poll(): E? {
        val iterator = linkedSet.iterator()
        return if (iterator.hasNext()) {
            val next = iterator.next()
            iterator.remove()
            next
        } else {
            null
        }
    }

    fun size(): Int = linkedSet.size

    fun hasNext(): Boolean = size() > 0
}
