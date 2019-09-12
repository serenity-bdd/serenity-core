# Appium Mac Installation

### Install Xcode or Upgrade to 8.3.3
* Goto the Mac Appstore and search for Xcode and click the Get button.
* After installation, install the Commandline Tools.
* Open a terminal and run `$ xcode-select --install`

#### Install Homebrew
* Open a terminal and run

```
$ /usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
```

* See additional install instructions [here](https://github.com/Homebrew/brew/blob/master/share/doc/homebrew/Installation.md#installation) for further details.
* Test install by running `$ brew --version`. You should see something like Homebrew 1.3.2.

#### Install Carthage
* Open a terminal and run `$ brew install carthage`
  * This is a dependency check for appium-doctor but not really needed for Android. Install it anyway as you may do iOS automation in the future.

### Install JAVA
* Open a terminal and check java version: `$ java -version`
	* If the version is less than 1.8 or the command is not recognized you need to install java.
	* If the version is 1.8 or greater then go to Ruby install.
* Go [here](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* Download and install jdk-8u144-macosx-x64.dmg
* Open Downloads folder and double click the DMG file.
* Double click the JDK 8 install icon.
* Click the Continue button.
* Click the Install button.
* Enter your password to install software on your system.

### Install Ruby
*** DO NOT use the default system OSX Ruby. Install a newer version with a Ruby package manager RVM (below instructions) or RBENV.

* Open a terminal and check ruby version: `$ ruby -v`
	* If the version is less than 2.3 or the command is not recognized you need to install ruby.
	* run `$ \curl -sSL https://get.rvm.io | bash -s stable --ruby` in terminal. This will install RVM (Ruby Version Manager) and install the latest Ruby version.
	* Run `$ rvm list` and see which ruby version was installed. Note the version number for next step.
	* Run `rvm --default use 2.4.0` to set the version as default. e.g. 2.4.0 instead of 2.x.x...

### Install Node (We need this for parallelization)
* Open a terminal and run `$ brew install node`.
* Test install by running `$ npm --version`. Verify version 5.3.X or greater is returned.
* Test Node is installed by running `$ node -v`. Verify version 8.5.X or greater is returned.

### Install Android Studio
* Click [here](https://developer.android.com/studio/index.html#mac-bundle) to download.
* Open downloads folder and double click the Android Studio DMG file.
* Drag and drop the Android Studio icon over to Applications folder. Close the install dialog.
* Open Finder and goto Applications folder. Double click Android Studio.
* Select "I do not have a previous version of studio or I do not want to import my settings" and click OK button.
* Click Next.
* Select Standard Installation and click the Next button.
* Click the Finish button.
* Enter password to allow HAXM to make changes to your system. HAXM is very import for emulator perforamce.
* Click the Finish button.

### Install Android 6.0 SDK 23
* Click the Configure button. It's on the lower right side of the Android Studio welcome dialog.
* Click SDK Manager in the configuration dropdown.
* Select Android 6.0 (Marshmallow) SDK.
   * Why Android 6.0? It's the most common SDK version used throughout the world and is a good starting point until you get more comfortable with mobile automation. See [here](https://www.appbrain.com/stats/top-android-sdk-versions)!
* Click the "Show Package Details" checkbox on lower right.
* Check the following SDK dependencies to install:
   * Android SDK Platform 23
   * Sources for Android 23
   * Intel x86 Atom System Image.
   * Intel x86 Atom_64 System Image.
   * Google API's Intel x86 Atom System Image
   * Google API's Intel X86 Atom_64 System Image
* Click the Apply button.
* On the next screen click the Accept radio button.
* *** Take note of your SDK Path! It's at the top of the Component Installer dialog. e.g SDK Path: /Users/YourUser/Library/Android/sdk
* Click the Next button to start the installation of SDK 23 dependencies. This will take a whie to complete...
* When everything has completed downloading, click the Finish button.
* Click the OK button to close the Component Installer dialog.
* Close Android Studio

### Add Environment Variables
* Determine which shell you use. Open a terminal and run `$ echo $SHELL`.
* Based on your shell, you need to edit your profile. e.g. `$ vi ~/.bash_profile or ~/.profile or ~/.zshrc`.
* Add the following variables.
	* export ANDROID_HOME=//Users/YourUser/Library/Android/sdk
	* export JAVA_HOME=$(/usr/libexec/java_home)
	* Add the Android sdk paths to your existing PATH=$PATH variable. e.g. :/Users/**your-user-name**/android-sdk-macosx/sdk/tools:/Users/**your-user-name**/android-sdk-macosx/sdk/platform-tools:/Users/**your-user-name**/android-sdk-macosx/sdk/build-tools:
* Save profile. `Press shift + :` type `wq!` and press enter.
* Force close/quit all your terminal windows and open a new one.

### Test Environment Variables
* Run `$ java -version` in terminal. You should see something close to this `java version "1.8.0_144"` or greater is returned.
* Run `$ ruby -v` in terminal. You should see something close to this `ruby 2.4.1` or greater.
* Run `$ emulator -help`. You should see menu options for android emulator manager.
* If any of the above is not working check to make sure the install locations are correct and reflected correctly in the path environment variable.

### Test ADB is installed.
* Run `$ adb` in CMD. You should see this `Android Debug Bridge version 1.0.39` or greater along wiht additional menu options.

### Create Android Emulator
* Run `$ android create avd -n EM1 -k "system-images;android-23;google_apis;x86"` in terminal.
* Enter NO to not create a custom hardware profile.
* Run the emulator: `$ANDROID_HOME/tools/emulator -avd EM1`
	* Verify avd output on startup includes:
	* Hax is enabled
	* HAX is working and emulator runs in fast virt mode.
	* Verify emulator fully starts and you see the android home/desktop screen.
* Note: Emulator can also be created via the AVD Manager IDE inside of Android Studio. However, you would need to create a new project or import one to see this menu option.

### Install Appium Desktop
* Download and install Appium Desktop [here](https://github.com/appium/appium-desktop/releases/download/v1.2.1/appium-desktop-1.2.1.dmg)

### Install Appium Doctor via NPM
* Run `$ npm install -g appium-doctor`
* Run `$ appium-doctor` (checks setup is correct on machine)
	*  	Verify "info AppiumDoctor Everything looks good, bye!" is displayed.
	* If there are things missing please go back to the install instructions above or environment variable setup.

### Install Appium via NPM
* Run `$ npm install -g appium`
	* Verify appium installed correctly by doing `$ appium -v`

### Install Sublime (We should all use same editor)
* Download and install [here](https://download.sublimetext.com/Sublime%20Text%20Build%203143.dmg)

### Executing the tests
To run the sample project, you can run `mvn verify` from the command line.

Before running, should start appium with port = 4724 on Android and with port  = 4723 on iOS

- The tests will run using Android.

```
          $ mvn clean verify -Dproperties=android.properties
```

- The tests will run using iOS.

```
          $ mvn clean verify -Dproperties=ios.properties
```

If you want to use Cucumber. You can add more _-Pcucumber_. e.g:

```
          $ mvn clean verify -Dproperties=android.properties -Pcucumber
```

Or

```
          $ mvn clean verify -Dproperties=ios.properties -Pcucumber
```
