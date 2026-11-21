/**
 * Staging area for code that is expected to move into SeriouslyCommonLib (SCL) later.
 *
 * <p>
 * Code is developed and proven here first, then moved to SCL once it has settled. To keep that move a plain package
 * rename:
 * <ul>
 * <li>Classes here may depend only on SCL, WPILib and the JDK, never on {@code competition.*}.</li>
 * <li>Robot-specific choices (names, CAN IDs, configs) stay in the robot code and reach this package through hooks the
 * robot implements.</li>
 * <li>Each class's Javadoc names its target SCL package.</li>
 * </ul>
 *
 * <p>
 * Current contents (target {@code xbot.common.injection.electrical_contract}):
 * {@link competition.scl_staging.XSwerveModuleWiringContract}, {@link competition.scl_staging.SwerveModuleWiring},
 * {@link competition.scl_staging.SwerveMotorSpec}.
 *
 * <p>
 * When moving a class to SCL: copy it into the target package, publish an SCL release, bump
 * {@code SeriouslyCommonLibVersion} in build.gradle, update imports here, then delete the staged copy.
 */
package competition.scl_staging;
