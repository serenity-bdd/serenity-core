package net.serenitybdd.reports.model

import java.time.LocalDateTime

class ReportInfo(val title: String,
                 val link: String,
                 val tagCategoryTitle: String,
                 val version: String,
                 val date: LocalDateTime)