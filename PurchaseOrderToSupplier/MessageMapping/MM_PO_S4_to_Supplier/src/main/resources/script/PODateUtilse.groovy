// Converts S/4HANA date (yyyy-MM-dd) to supplier format (dd.MM.yyyy)
// Example: 2026-11-10  ->  10.11.2026
def String formatDate(String inputDate) {
    if (inputDate == null || inputDate.trim().isEmpty()) {
        return ""
    }
    Date d = Date.parse("yyyy-MM-dd", inputDate.trim())
    return d.format("dd.MM.yyyy")
}
