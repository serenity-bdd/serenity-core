# Selenium 3 upgrade plan

X Dependency version numbers in property file
X Update JDK version
X Update Guava
X Update Selenium dependencies
- Identify references to Guava and remove where possible
- Get rid of lambdaj (-> Java 8 and maybe vavr)
- Get rid of Jodatime (-> JavaTime)
