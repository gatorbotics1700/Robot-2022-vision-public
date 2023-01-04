package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatorCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import frc.robot.RobotMap;
import frc.robot.OI;
import edu.wpi.first.wpilibj.SPI;
import com.kauailabs.navx.frc.AHRS; 
    

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.CANSparkMax.IdleMode;
//import com.revrobotics.SparkMaxRelativeEncoder;

//TO DO
//figure out errors w/sparkmax and falcons

public class DriveSubsystem{
    //creates motor controller objects
    public static CANSparkMax RF = new CANSparkMax(RobotMap.RIGHTFRONT, MotorType.kBrushless);
    public static CANSparkMax RB = new CANSparkMax(RobotMap.RIGHTBACK, MotorType.kBrushless);
    public static CANSparkMax RM = new CANSparkMax(RobotMap.RIGHTMID, MotorType.kBrushless); 
    public static CANSparkMax LF = new CANSparkMax(RobotMap.LEFTFRONT, MotorType.kBrushless);
    public static CANSparkMax LB = new CANSparkMax(RobotMap.LEFTBACK, MotorType.kBrushless);
    public static CANSparkMax LM = new CANSparkMax(RobotMap.LEFTMID, MotorType.kBrushless);


    //public static RelativeEncoder RF_ENCODER = new RelativeEncoder(RF);
    //public static RelativeEncoder LF_ENCODER = new RelativeEncoder(LF);
   
    //creates Navx Gyro and Encoder values 
    public static AHRS Navx = new AHRS(SPI.Port.kMXP);
    private double tareGyro = 0.0;
    private double tareEncoder = 0.0; 

    public final double MAXVOLTAGE = 0.9;
    public final double ADJFALCONS = 0.005;

    public DriveSubsystem(){
        setMotorDirection();
        setCurrentLimits();
    }

    public void driveTank(double parameterLeftSpeed, double parameterRightSpeed){
        LF.set(parameterLeftSpeed);
        LM.set(parameterLeftSpeed);
        LB.set(parameterLeftSpeed);
        RF.set(parameterRightSpeed);
        RM.set(parameterRightSpeed);
        RB.set(parameterRightSpeed); 
    }

    //calls driveTank with joystick inputs as parameters
    public void drive(){
        double turnVelocity = -conversion(OI.rightJoystick.getRawAxis(0), true);
        double ForwardVelocity = -conversion(OI.leftJoystick.getRawAxis(1), false);
      
        //deadband so the robot doesn't wander around
        if (Math.abs(turnVelocity) == 0.0 && Math.abs(ForwardVelocity) == 0.0){
            LimelightSubsystem.setState(LimelightStates.SEARCH);
        }
        if (Math.abs(turnVelocity) < 0.2){
            turnVelocity = 0;
        }
        if (Math.abs(ForwardVelocity) < 0.2){
            ForwardVelocity = 0;
        }

        double finalSpeedLeft = Math.min(0.86*ForwardVelocity-0.8*turnVelocity+ADJFALCONS, MAXVOLTAGE);
        double finalSpeedRight = Math.min((0.86*ForwardVelocity+0.8*turnVelocity), MAXVOLTAGE-ADJFALCONS);

        driveTank(finalSpeedLeft, finalSpeedRight);
    }  

    public void checkRPM() {
        /*
        System.out.println("RF" + (RF.getVelocity()/2048/100)*1000*60);
        System.out.println("RB" + (RB.getVelocity()/2048/100)*1000*60);
        System.out.println("LB" + (LB.getVelocity()/2048/100)*1000*60);
        System.out.println("LF" + (LF.getVelocity()/2048/100)*1000*60);
        */
        //werid math hopefully not needed. 

    }

    public void setMotorDirection(){
        LF.setInverted(true); //s made true //was t before j resolved merge conflicts on sat 4/2 10:30am - test to see if this works though
        LB.setInverted(true); //was t
        RF.setInverted(false); //was f
        RB.setInverted(false); //was f
    }

    public void setCurrentLimits(){
        /*
        The stator current limit is what limits the motor. So if you want to limit acceleration, motor heat, etc. you should limit the stator current.
        The supply current is the same as the current being drawn through the PDP, so this is what you should limit to prevent breaker trips.
        */

        //bool enable, double currentLimit, double triggerThresholdCurrent, double triggerThresholdTime
        //when current exceeds triggerThresholdCurrent for triggerThresholdTime, the current will be limited to currentLimit
       /*
        LF.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration(true, 40, 60, 2));
        LB.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration(true, 40, 60, 2));
        RF.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration(true, 40, 60, 2));
        RB.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration(true, 40, 60, 2));

        LF.configStatorCurrentLimit(new StatorCurrentLimitConfiguration(true, 80, 105, 0.1));
        LB.configStatorCurrentLimit(new StatorCurrentLimitConfiguration(true, 80, 105, 0.1));
        RF.configStatorCurrentLimit(new StatorCurrentLimitConfiguration(true, 80, 105, 0.1));
        RB.configStatorCurrentLimit(new StatorCurrentLimitConfiguration(true, 80, 105, 0.1));
        */
        //couldn't find for sparkmax, could be kSmartCurrentConfig

    }

    public void setCoastMode(){
        LF.setIdleMode(IdleMode.kCoast);
        LB.setIdleMode(IdleMode.kCoast);
        LM.setIdleMode(IdleMode.kCoast);
        RF.setIdleMode(IdleMode.kCoast);
        RB.setIdleMode(IdleMode.kCoast);
        RM.setIdleMode(IdleMode.kCoast);
    }

    public void setBrakeMode(){
        LF.setIdleMode(IdleMode.kBrake);
        LB.setIdleMode(IdleMode.kBrake);
        LM.setIdleMode(IdleMode.kBrake);
        RB.setIdleMode(IdleMode.kBrake);
        RF.setIdleMode(IdleMode.kBrake);
        RM.setIdleMode(IdleMode.kBrake);
    }

    public double countsToInches(double ticks){ 
      double ticksPerRev = 2048.0; // ticks per revolution")
      double gearRatio = 1.0/7.0;
      double wheelRadius = 3.0; // in inches
      double dticks = (double)ticks;
      double inches = (dticks/ticksPerRev)*gearRatio*2*wheelRadius*Math.PI;
      //System.out.println("Inches: " + inches);
      return inches;
    } 
    
    public double getDegrees(){
        System.out.println("Navx angle: "+ Navx.getAngle());
        //System.out.println("Tare: " + tareGyro);
        return Navx.getAngle() - tareGyro; //need to figure out why this is printing 0.
        
    }

    public void resetGyro(){
        tareGyro = Navx.getAngle();
    }

    public double getRate(){
        return Navx.getRate();
    }

    public void resetEncoders(){
        //tareEncoder = LB.getSelectedSensorPosition(); 
        // taring from 1 encoder's reading because all encoders increasing by same amount
    }
    
    public double getTicks(){
        return LB.getEncoder().getPosition() - tareEncoder; 
    }
    

    public double getDriveVelocity(){
        return LB.get();
    }
 
    public double conversion(double joystickValue, boolean isTurn){
        double speed;

        double a = 0.03; //minVoltage, CHANGE AFTER TESTING
        double b = 0.05; //linear coefficient, CHANGE AFTER TESTING
        double c = 0.4; //quadratic coefficient, CHANGE AFTER TESTING
        double d = 0.52; //cubic coefficient, CHANGE AFTER TESTING
        if (Math.abs(joystickValue)>0.02){ //reduced from 0.07
            if (!isTurn){
                speed = Math.signum(joystickValue)*(a + b  * Math.abs((Math.pow(joystickValue, 1)))+ c  * Math.abs((Math.pow(joystickValue, 2))) + d  * Math.abs((Math.pow(joystickValue, 3))));
            } else{
                speed = Math.signum(joystickValue)*(a + b  * Math.abs(Math.pow(joystickValue, 1))+ c*2  * Math.abs(Math.pow(joystickValue, 2)));  
            }
            // if(joystickValue<0){
            //     speed = -speed;
            // }
        } else {
            speed = 0;
        }
        return speed;

    }

}