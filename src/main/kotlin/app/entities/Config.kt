package app.entities

import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "configuration")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
open class Config {
    @Id
    @GeneratedValue
    open var id: Long? = null

    @NotNull
    @Column(unique = true)
    open lateinit var key: String

    @NotNull
    open lateinit var value: String
}

@Entity
@Table(name = "parking_fee_configuration")
open class ParkingFeeConfigModel : Config()