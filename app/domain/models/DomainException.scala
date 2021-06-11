package domain.models

class DomainException(message: String) extends Exception(message)

class InvalidDataException(message: String) extends DomainException(message)
class ConflictException(message: String)    extends DomainException(message)
