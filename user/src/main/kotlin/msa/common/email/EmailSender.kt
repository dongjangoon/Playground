package msa.common.email

interface EmailSender {
    fun sendEmail(to: String, subject: String, content: String)
}
