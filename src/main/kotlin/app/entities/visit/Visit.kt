package app.entities.visit

import app.entities.Payment
import org.hibernate.annotations.Cascade
import org.hibernate.annotations.CascadeType.*
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity(name = "visits")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
open class Visit {
    @Id
    @GeneratedValue
    open var id: Long? = null

    @NotNull
    open var entryTime: Instant = Instant.now()

    @NotNull
    open var ticketCode: Long = 0L

    @OneToMany(
        mappedBy = "visit",
        targetEntity = Payment::class,
        cascade = [CascadeType.PERSIST, CascadeType.REFRESH],
    )
    @Cascade(SAVE_UPDATE)
    open var payments: List<Payment> = emptyList()
}

@Entity
open class OngoingVisit : Visit()

@Entity
open class FinishedVisit : Visit() {
    @CreationTimestamp
    open var exitTime: Instant = Instant.now()
}
