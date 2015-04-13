# language: de

Funktionalität: Ich möchte testen, wie Serenity und Cucumber mit deutscher Sprache umgehen.

  Szenariogrundriss: Google-Suche
    Angenommen Ich habe Google offen.
    Wenn Ich nach '<suchbegriff>' suche
    Dann Sehe ich u.a. das Ergebnis '<ergebnis>'
  Beispiele:
    | suchbegriff | ergebnis |
    | serenity selenium | The Serenity Reference Manual - Thucydides |
    | Hüpfburg | Hüpfburg - eBay Kleinanzeigen |
