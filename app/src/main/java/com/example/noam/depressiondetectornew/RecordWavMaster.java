package com.example.noam.depressiondetectornew;

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AudioEffect;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.LoudnessEnhancer;
import android.media.audiofx.NoiseSuppressor;
import android.media.audiofx.Visualizer;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.format.Time;
import android.util.Log;

import com.gauravk.audiovisualizer.visualizer.CircleLineVisualizer;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import me.bogerchan.niervisualizer.NierVisualizerManager;

public class RecordWavMaster {
    private static final int samplingRates[] = {8000, 6000, 5000, 4000, 3000,2000,1000,500};
    public static int SAMPLE_RATE = 16000;
    private AudioRecord mRecorder;
    public static byte[] bytes;
    private File mRecording;
    private short[] mBuffer;
    private String audioFilePath;
    public static NierVisualizerManager visualizerManager;
    private boolean mIsRecording = false;
  //  private String RECORD_WAV_PATH = MainActivity.getFilesDirPath() + File.separator + "AudioRecord";;//Environment.getExternalStorageDirectory() + File.separator + "AudioRecord";
    public static File latestRecFile;
    private String RECORD_WAV_PATH = Environment.getExternalStorageDirectory() + File.separator + "AudioRecord";

    /* Initializing AudioRecording MIC */
    public RecordWavMaster() {
        initRecorder();
    }

    /* Get Supported Sample Rate */
    public static int getValidSampleRates() {
        for (int rate : samplingRates) {
            int bufferSize = AudioRecord.getMinBufferSize(rate, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
            if (bufferSize > 0) {
                return rate;
            }
        }
        return SAMPLE_RATE;
    }

    /* Start AudioRecording */
    public void startRecording() {
        mIsRecording = true;
        mRecorder.startRecording();
        mRecording = getFile("raw");
        startBufferedWrite(mRecording);

        //return mRecorder.getAudioSessionId();
    }
    public int getSession(){
        return mRecorder.getAudioSessionId();
    }

    /* Stop AudioRecording */
    public String stopRecording() {
        try {
            mIsRecording = false;
            mRecorder.stop();
            latestRecFile = getFile("wav");
            rawToWave(mRecording, latestRecFile);
            Log.e("path_audioFilePath",audioFilePath);
            return audioFilePath;
        } catch (Exception e) {
            Log.e("Error saving file : ", e.getMessage());
        }
        return  null;
    }

    /* Release device MIC */
    public void releaseRecord() {

        mRecorder.release();
        mRecorder = null;
    }

    /*
    public static byte[] getBytes(){
        byte b[] = new byte[];
        return mBuffer;
    }
    */

    public static NierVisualizerManager getInstanceInit(){


        visualizerManager = new NierVisualizerManager();

        visualizerManager.init(new NierVisualizerManager.NVDataSource() {

            // skip some code...

            /**
             * Tell the manager about the data sampling interval.
             * @return the data sampling interval which is millisecond of unit.
             */
            @Override
            public long getDataSamplingInterval() {
                return 0L;
            }

            /**
             * Tell the manager about the data length of fft data or wave data.
             * @return the data length of fft data or wave data.
             */
            @Override
            public int getDataLength() {
                return bytes.length;
            }

            /**
             * The manager will fetch fft data by it.
             * @return the fft data, null will be ignored by the manager.
             */
            @Nullable
            @Override
            public byte[] fetchFftData() {
                return null;
            }

            /**
             * The manager will fetch wave data by it.
             * @return the wave data, null will be ignored by the manager.
             */
            @Nullable
            @Override
            public byte[] fetchWaveData() {
                // skip some code...
                return bytes;
            }
        });

        return visualizerManager;

    }

    /* Initializing AudioRecording MIC */
    private void initRecorder() {
        SAMPLE_RATE = getValidSampleRates();
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        mBuffer = new short[bufferSize];
        mRecorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        bytes = new byte[bufferSize * 2];
        //AudioEffect.Descriptor[] d = NoiseSuppressor.queryEffects();
        if(AutomaticGainControl.isAvailable())
        {
            AutomaticGainControl agc =AutomaticGainControl.create(mRecorder.getAudioSessionId());
            //agc.g
            Log.d("AudioRecord", "AGC is " + (agc.getEnabled()?"enabled":"disabled"));
            agc.setEnabled(true);
            Log.d("AudioRecord", "AGC is " + (agc.getEnabled()?"enabled":"disabled" +" after trying to enable"));
        }else
        {
            Log.d("AudioRecord", "AGC is unavailable");
        }

        if(NoiseSuppressor.isAvailable()){

            NoiseSuppressor ns = NoiseSuppressor.create(mRecorder.getAudioSessionId());
            Log.d("AudioRecord", "NS is " + (ns.getEnabled()?"enabled":"disabled"));
            ns.setEnabled(true);
            Log.d("AudioRecord", "NS is " + (ns.getEnabled()?"enabled":"disabled" +" after trying to disable"));
        }else
        {
            Log.d("AudioRecord", "NS is unavailable");
        }
        if(AcousticEchoCanceler.isAvailable()){

            AcousticEchoCanceler aec = AcousticEchoCanceler.create(mRecorder.getAudioSessionId());
            Log.d("AudioRecord", "AEC is " + (aec.getEnabled()?"enabled":"disabled"));
            aec.setEnabled(true);
            Log.d("AudioRecord", "AEC is " + (aec.getEnabled()?"enabled":"disabled" +" after trying to disable"));

        }else
        {
            Log.d("AudioRecord", "aec is unavailable");
        }
        if(AcousticEchoCanceler.isAvailable()){

            AcousticEchoCanceler aec = AcousticEchoCanceler.create(mRecorder.getAudioSessionId());
            Log.d("AudioRecord", "AEC is " + (aec.getEnabled()?"enabled":"disabled"));
            aec.setEnabled(true);
            Log.d("AudioRecord", "AEC is " + (aec.getEnabled()?"enabled":"disabled" +" after trying to disable"));

        }else
        {
            Log.d("AudioRecord", "aec is unavailable");
        }
        LoudnessEnhancer x = new LoudnessEnhancer(mRecorder.getAudioSessionId());
        x.setTargetGain(0);

        new File(RECORD_WAV_PATH).mkdir();
    }
    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;

    }
    /* Writing RAW file */
    private void startBufferedWrite(final File file) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DataOutputStream output = null;

                try {
                    output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));

                    while (mIsRecording) {
                        double sum = 0;
                        int readSize = mRecorder.read(mBuffer, 0, mBuffer.length);
                        for (int i = 0; i < readSize; i++) {
                            //bytes[i * 2] = (byte) (mBuffer[i] & 0x00FF);
                            //bytes[(i * 2) + 1] = (byte) (mBuffer[i] >> 8);
                            output.writeShort(mBuffer[i]);
                            sum += mBuffer[i] * mBuffer[i];
                        }
                        if (readSize > 0) {
                            final double amplitude = sum / readSize;
                        }
                    }
                } catch (IOException e) {
                    Log.e("Error writing file : ", e.getMessage());
                } finally {

                    if (output != null) {
                        try {
                            output.flush();
                        } catch (IOException e) {
                            Log.e("Error writing file : ", e.getMessage());
                        } finally {
                            try {
                                output.close();
                            } catch (IOException e) {
                                Log.e("Error writing file : ", e.getMessage());
                            }
                        }
                    }
                }
            }
        }).start();
    }

    /* Converting RAW format To WAV Format*/
    private void rawToWave(final File rawFile, final File waveFile) throws IOException {

        byte[] rawData = new byte[(int) rawFile.length()];
        DataInputStream input = null;
        try {
            input = new DataInputStream(new FileInputStream(rawFile));
            input.read(rawData);
        } finally {
            if (input != null) {
                input.close();
            }
        }
        DataOutputStream output = null;
        try {
            output = new DataOutputStream(new FileOutputStream(waveFile));
            // WAVE header
            writeString(output, "RIFF"); // chunk id
            writeInt(output, 36 + rawData.length); // chunk size
            writeString(output, "WAVE"); // format
            writeString(output, "fmt "); // subchunk 1 id
            writeInt(output, 16); // subchunk 1 size
            writeShort(output, (short) 1); // audio format (1 = PCM)
            writeShort(output, (short) 1); // number of channels
            writeInt(output, SAMPLE_RATE); // sample rate
            writeInt(output, SAMPLE_RATE * 2); // byte rate
            writeShort(output, (short) 2); // block align
            writeShort(output, (short) 16); // bits per sample
            writeString(output, "data"); // subchunk 2 id
            writeInt(output, rawData.length); // subchunk 2 size
            // Audio data (conversion big endian -> little endian)
            short[] shorts = new short[rawData.length / 2];
            ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
            ByteBuffer bytes = ByteBuffer.allocate(shorts.length * 2);
            for (short s : shorts) {
                bytes.putShort(s);
            }
            output.write(bytes.array());
        } finally {
            if (output != null) {
                output.close();
                rawFile.delete();
            }
        }


    }

    /* Get file name */
    private File getFile(final String suffix) {
        Time time = new Time();
        time.setToNow();
        audioFilePath = time.format("%Y%m%d%H%M%S");
        return new File(RECORD_WAV_PATH, time.format("%Y%m%d%H%M%S") + "." + suffix);

    }

    private void writeInt(final DataOutputStream output, final int value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
        output.write(value >> 16);
        output.write(value >> 24);
    }

    private void writeShort(final DataOutputStream output, final short value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
    }

    private void writeString(final DataOutputStream output, final String value) throws IOException {
        for (int i = 0; i < value.length(); i++) {
            output.write(value.charAt(i));
        }
    }

    public String getFileName (final String time_suffix) {
        return (RECORD_WAV_PATH+time_suffix+ "." + "wav");
    }

    public Boolean getRecordingState () {
        if(  mRecorder.getRecordingState() == AudioRecord.RECORDSTATE_STOPPED) {
            return false;
        }
        return true;
    }
}