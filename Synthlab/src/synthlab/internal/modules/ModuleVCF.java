package synthlab.internal.modules;

import synthlab.api.Port;
import synthlab.api.Scheduler;
import synthlab.internal.BasicModule;
import synthlab.internal.BasicPort;

public class ModuleVCF extends BasicModule
{
  
  //output signal
  private double  valueOut;
  
  //
  private final int frameRate_;
  private final double maxVolume = 1;
  private final double minVolume = -1;
  
  // for remenber precent computed value 
  private double       lowPassPole  = 0.0;
  private double       bandPassPole = 0.0;

  public ModuleVCF()
  {
    super("VCF");
    // Input port
    addInput(new BasicPort("iSignal", 0, Port.ValueType.CONTINUOUS,
        Port.ValueUnit.AMPLITUDE, new Port.ValueRange(-1, 1)));
    addInput(new BasicPort("iCutOff", 100, Port.ValueType.CONTINUOUS,
        Port.ValueUnit.HERTZ, new Port.ValueRange(100, 5000)));
    addInput(new BasicPort("iResonance", 0, Port.ValueType.DISCRETE,
        Port.ValueUnit.DECIBELS, new Port.ValueRange(0.0, 3.0)));

    // OutPut port
    addOutput(new BasicPort("oLowPass", 0, Port.ValueType.CONTINUOUS,
        Port.ValueUnit.AMPLITUDE, new Port.ValueRange(-1, 1)));
    addOutput(new BasicPort("oHighPass", 0, Port.ValueType.CONTINUOUS,
        Port.ValueUnit.AMPLITUDE, new Port.ValueRange(-1, 1)));
    addOutput(new BasicPort("oBandPass", 0, Port.ValueType.CONTINUOUS,
        Port.ValueUnit.AMPLITUDE, new Port.ValueRange(-1, 1)));
    addOutput(new BasicPort("oNotch", 0, Port.ValueType.CONTINUOUS,
        Port.ValueUnit.AMPLITUDE, new Port.ValueRange(-1, 1)));

    frameRate_ = 44100;
    
  }

  @Override
  public void compute()
  {
    synchronized (getInput("iSignal"))
    {
      synchronized (getInput("iCutOff"))
      {
        synchronized (getInput("iResonance"))
        {
          synchronized (getOutput("oLowPass"))
          {
            synchronized (getOutput("oHighPass"))
            {
              synchronized (getOutput("oBandPass"))
              {
                synchronized (getOutput("oNotch"))
                {
                  getInput("iSignal").getValues().clear();
                  getInput("iCutOff").getValues().clear();
                  getInput("iResonance").getValues().clear();
                  getOutput("oLowPass").getValues().clear();
                  getOutput("oHighPass").getValues().clear();
                  getOutput("oBandPass").getValues().clear();
                  getOutput("oNotch").getValues().clear();

                  for (int i = 0; i < Scheduler.SamplingBufferSize; ++i)
                  {
                    
                    double sigalIn = getInput("iSignal").getValues().getDouble();
                    
                    double cutOff = getInput("iCutOff").getValues().getDouble();
                   
                    double resonance = Math.pow(10.0, getInput("iResonance").getValues().getDouble() / 10.0);

                    final double rc = 1.0 / (4.0 * Math.PI * cutOff);
                    final double alpha = (1.0 / frameRate_)/ (1.0 / frameRate_ + rc);
                    
                    double lowPass = lowPassPole + alpha * (sigalIn - lowPassPole);
                    lowPassPole = lowPass;
                    
                    double highPass = sigalIn - lowPass;
                    double bandPass = bandPassPole + alpha * (highPass - bandPassPole);
                    bandPassPole = bandPass;
                    double notch = sigalIn - bandPass;
                    
                    lowPass += bandPass * resonance;
                    highPass += bandPass * resonance;
                    bandPass += bandPass * resonance;
                    notch +=notch*resonance;
  
                    
                    setValue(lowPass);
                    getOutput("oLowPass").getValues().putDouble(valueOut);

                    setValue(highPass);
                    getOutput("oHighPass").getValues().putDouble(valueOut);
                    
                    setValue(bandPass);
                    getOutput("oBandPass").getValues().putDouble(valueOut);

                    setValue(notch);
                    getOutput("oNotch").getValues().putDouble(valueOut);
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  public double getValue()
  {
    return valueOut;
  }

  public void setValue(double val)
  {
    if (val > minVolume && val < maxVolume)
      valueOut = val;
    else if (val > maxVolume)
      valueOut = maxVolume;
    else if (val < minVolume)
      valueOut = minVolume;
  }

}
