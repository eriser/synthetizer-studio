package synthlab.internal.modules;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import synthlab.internal.BasicModule;
import synthlab.internal.BasicPort;

public class ModuleOut extends BasicModule
{
  public ModuleOut()
  {
    super("Out");
    addInput(new BasicPort("iSignal", 0));
    baseFormat = new AudioFormat(44100.0f, 16, 1, true, false);
    int nFrames = (int) Math.ceil(44100 * 1.0);
    data = ByteBuffer.allocate(2 * 4410);
    stream = new AudioInputStream(new ByteArrayInputStream(data.array()),
        baseFormat, nFrames);

  }

  @Override
  public void compute()
  {
    double volIn = getInput("iSignal").getValue();
    
    if (volIn < 1 && volIn > -1)
    {
      sampleCompteur++;
      data.putShort((short) volIn);

    }

    try
    {
      if (sampleCompteur == 4410)
      {
        Clip clip = AudioSystem.getClip();
        clip.open(stream);
        clip.start();
        clip.drain();
        data.clear();
      }
    }
    catch (Exception e)
    {

    }
  }

  int                 sampleCompteur;
  private AudioFormat baseFormat;
  private ByteBuffer  data;
  AudioInputStream    stream;
}
