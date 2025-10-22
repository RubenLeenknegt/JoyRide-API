package leafcar.backend.mappers

import kotlinx.datetime.LocalDate
import leafcar.backend.EnvironmentSetup
import leafcar.backend.dao.UserEntity
import leafcar.backend.domain.User
import leafcar.backend.domain.UserType
import leafcar.backend.dto.UserDto
import leafcar.backend.mappers.UserMapper.toDomain
import leafcar.backend.mappers.UserMapper.toDto
import leafcar.backend.mappers.UserMapper.toUserDto
import leafcar.backend.repository.UserRepository
import leafcar.backend.services.AuthService
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertIs

class UserMapperTest {
    private val authservice = AuthService(UserRepository())

    @BeforeEach
    fun setUp() {
        EnvironmentSetup.setup()
    }

    @Test
    fun toDomainIsUserEqualsTrue() {
        val plainPassword = "somerandompassword"
        val user = authservice.registration(
            emailAddress = "test123@hotlook.com",
            password = plainPassword,
            firstName = "Peter",
            lastName = "Janssen",
            birthDate = LocalDate.parse("1998-05-29"),
            userType = UserType.RENTER,
            bankAccount = null,
            bankAccountName = null,
            vehicleLocation = null
        )

        val entity = transaction { UserEntity.findById(user!!.id) }
        assertIs<User>(entity!!.toDomain())
        assertEquals(user, entity.toDomain())
    }

    @Test
    fun toUserDtoIsUserDtoEqualsTrue() {
        val plainPassword = "somerandompassword"
        val user = authservice.registration(
            emailAddress = "test123@hotlook.com",
            password = plainPassword,
            firstName = "Peter",
            lastName = "Janssen",
            birthDate = LocalDate.parse("1998-05-29"),
            userType = UserType.RENTER,
            bankAccount = null,
            bankAccountName = null,
            vehicleLocation = null
        )

        val entity = transaction { UserEntity.findById(user!!.id) }
        assertIs<UserDto>(entity!!.toUserDto())
        assertEquals(user!!.id, entity.toUserDto().id)
        assertEquals(user.emailAddress, entity.toUserDto().emailAddress)
        assertEquals(user.firstName, entity.toUserDto().firstName)
        assertEquals(user.lastName, entity.toUserDto().lastName)
        assertEquals(user.birthDate, entity.toUserDto().birthDate)
        assertEquals(user.userType, entity.toUserDto().userType)
        assertEquals(user.bankAccount, entity.toUserDto().bankAccount)
        assertEquals(user.bankAccountName, entity.toUserDto().bankAccountName)
        assertEquals(user.vehicleLocation, entity.toUserDto().vehicleLocation)
    }

    @Test
    fun userToDtoIsUserDtiEqualsTrue() {
        val plainPassword = "somerandompassword"
        val user = authservice.registration(
            emailAddress = "test123@hotlook.com",
            password = plainPassword,
            firstName = "Peter",
            lastName = "Janssen",
            birthDate = LocalDate.parse("1998-05-29"),
            userType = UserType.RENTER,
            bankAccount = null,
            bankAccountName = null,
            vehicleLocation = null
        )

        assertIs<UserDto>(user!!.toDto())
        val userDto = user.toDto()
        assertEquals(user.id, userDto.id)
        assertEquals(user.emailAddress, userDto.emailAddress)
        assertEquals(user.firstName, userDto.firstName)
        assertEquals(user.lastName, userDto.lastName)
        assertEquals(user.birthDate, userDto.birthDate)
        assertEquals(user.userType, userDto.userType)
        assertEquals(user.bankAccount, userDto.bankAccount)
        assertEquals(user.bankAccountName, userDto.bankAccountName)
        assertEquals(user.vehicleLocation, userDto.vehicleLocation)
    }
}