package ninja.util.kotlinjpa

import java.io.Serializable
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Version
import javax.persistence.GeneratedValue
import javax.persistence.Persistence
import javax.persistence.EntityManager
import org.apache.openjpa.jdbc.meta.MappingTool


[Entity]
public open class Person(name : String? = null, age : Int? = null) : Serializable {
    [Id] [GeneratedValue]
    public var id : Long? = null
    [Version]
    public var version : Long? = null
    public var name : String?
    public var age : Int?

    {
        this.name = name
        this.age = age
    }

    public override fun hashCode() : Int =
        HashCodeBuilder()
            .append(id)
            .append(version)
            .append(name)
            .append(age)
            .toHashCode()

    public override fun equals(other: Any?) : Boolean {
        if (this == other) {
            return true
        } else if (other is Person) {
            return EqualsBuilder()
                .append(id, other.id)
                .append(version, other.version)
                .append(name, other.name)
                .append(age, other.age)
                .isEquals()
        } else {
            return false
        }
    }

    public override fun toString() : String {
        return ToStringBuilder(this)
            .append("id", id)
            .append("version", version)
            .append("name", name)
            .append("age", age)
            .toString()
    }
}


private fun inTransaction<R>(em : EntityManager, f : (EntityManager) -> R) : R {
    try {
        em.getTransaction().begin()
        val ret = f(em)
        em.getTransaction().commit()
        return ret
    } catch (e : Exception) {
        em.getTransaction().rollback()
        throw e
    }
}

public fun main(args : Array<String>) {
    val em = Persistence
            .createEntityManagerFactory("person")
            .createEntityManager()

    inTransaction(em) {
        em.persist(Person(name="Foo", age=10))
        em.persist(Person(name="Bar", age=20))
    }

    val res = em.createQuery("SELECT p FROM Person p", javaClass<Person>()).getResultList()
    for (p in res) {
        println(p)
    }

    em.close()
}

