package core

/*
* TODO: Write tests
*/
enum class RequestAction(val code: Int) {
    Exit(1),
    Entry(2),
    Unrecognized(-1);

    companion object {
        fun fromCode(code: Int): RequestAction = when (code) {
            Exit.code -> Exit
            Entry.code -> Entry
            else -> Unrecognized
        }
    }

}
