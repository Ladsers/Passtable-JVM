![cover](https://github.com/Ladsers/Passtable-JVM/raw/developing/.github/readme/github_readme_cover.png)

**The Passtable project**:ㅤ[Android app](https://github.com/Ladsers/Passtable-Android)ㅤ|ㅤ[Windows app](https://github.com/Ladsers/Passtable-for-Windows)ㅤ|ㅤ<ins>JVM app (Linux & macOS)</ins>ㅤ|ㅤ[Library](https://github.com/Ladsers/Passtable-Lib)
</br></br>


## Passtable
Console application for storing passwords and confidential data. Written in Kotlin using its own core library. Requires Java Runtime Environment 8+ to run. Can be run on Linux without a graphical shell. Opens ".passtable" files.

```
git clone --recursive https://github.com/Ladsers/Passtable-JVM.git
```
#### How to run on Ubuntu?
1. Install some JRE (only 8+ are supported):
```
$ sudo apt install openjdk-8-jre-headless
```
2. Download "Passtable-22.11.0.jar" from releases;
3. Open terminal in the directory where the file was downloaded;
4. Launch the application:
```
$ java -jar Passtable-22.11.0.jar 
```
*Use the latest version instead of 22.11.0!*

### Features
🔸 **Free & No ads** </br>
The application does not contain paid functions and advertising.

🔸 **Encryption** </br>
Strong AES encryption based on the popular open source cryptography library [Bouncy Castle](https://www.bouncycastle.org/).

🔸 **File approach** </br>
Passtable stores data in separate files, and not in the application itself. The advantage of this approach is that data sets can exist independently of each other and, if necessary, can easily be moved to another device (including Android smartphone).

🔸 **Quick data sorting** </br>
Add tags when creating elements in one touch, and then quickly find your data using them.

🔸 **Log'n'Pass system** </br>
Semi-automatic sequential copying of data to the clipboard for quick authentication on sites and applications.

### Screenshots
![screenshot](https://github.com/Ladsers/Passtable-JVM/raw/developing/.github/readme/github_ubuntu.png)

### Contributing
Here are some ways you can contribute:
+ [Submit Issues](https://github.com/Ladsers/Passtable-JVM/issues/new/choose) in Passtable-JVM or another the Passtable project repository;
+ [Submit Pull requests](https://github.com/Ladsers/Passtable-JVM/pulls) with fixes and features;
+ [Share your Ideas](https://github.com/Ladsers/Passtable-JVM/discussions/categories/ideas) about application.

#### Branches
+ **developing** is the most relevant branch. Contains the latest features, but is not stable. The default branch for Pull requests. Without testing. <ins>It is strongly not recommended to use for real data.</ins>
+ **beta** branch is used for debugging and fixing bugs in pre-release.
+ **stable** branch contains the source code from which release binaries are built.

### License
The code in this repository is licensed under the [Apache License 2.0](https://github.com/Ladsers/Passtable-JVM/blob/developing/LICENSE.md). The third party resources used are listed in [NOTICE.md](https://github.com/Ladsers/Passtable-JVM/blob/developing/NOTICE.md).
</br></br>
The Passtable logo can be used in applications or articles only when the Passtable project is explicitly mentioned.
