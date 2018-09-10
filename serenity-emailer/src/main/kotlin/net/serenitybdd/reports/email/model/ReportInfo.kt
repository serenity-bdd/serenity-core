package net.serenitybdd.reports.email.model

import java.time.LocalDateTime

class ReportInfo(val title: String,
                 val tagCategoryTitle: String,
                 val version: String,
                 val date: LocalDateTime)