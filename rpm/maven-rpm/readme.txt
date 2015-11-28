This project creates a RPM from a maven release.
Used input:
http://stackoverflow.com/questions/7532928/how-do-i-install-maven-with-yum

Different examples for downloading a zip file
http://stackoverflow.com/questions/2741806/maven-downloading-files-from-url

// Nice but not unzipped
			<plugin>
				<groupId>org.codehaus.mojo</groupId>
				<artifactId>wagon-maven-plugin</artifactId>
				<version>1.0-beta-5</version>
				<executions>
					<execution>
						<id>download-maven</id>
						<phase>prepare-package</phase>
						<goals>
							<goal>download-single</goal>
						</goals>
						<configuration>
							<url>http://mirror.olnevhost.net</url>
							<fromFile>pub/apache/maven/maven-3/3.2.1/binaries/apache-maven-3.2.1-bin.zip</fromFile>
							<toDir>${project.build.directory}/maven</toDir>
						</configuration>
					</execution>
				</executions>
			</plugin>