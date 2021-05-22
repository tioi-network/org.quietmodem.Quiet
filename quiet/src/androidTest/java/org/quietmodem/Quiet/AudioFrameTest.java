package org.quietmodem.Quiet;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class AudioFrameTest {

    @Test
    public void testTransmitAndReceive() throws IOException {
        FrameReceiverConfig receiverConfig = null;
        FrameTransmitterConfig transmitterConfig = null;

        try {
            /* if you are running a MainActivity
             * then replace InstrumentationRegistry.getTargetContext()
             * with `this`
             */
            transmitterConfig = new FrameTransmitterConfig(
                    InstrumentationRegistry.getInstrumentation().getTargetContext(),
                    "audible-7k-channel-0");
            receiverConfig = new FrameReceiverConfig(
                    InstrumentationRegistry.getInstrumentation().getTargetContext(),
                    "audible-7k-channel-0");
        } catch (IOException e) {
            fail("could not build configs");
        }

        FrameReceiver receiver = null;
        FrameTransmitter transmitter = null;
        try {
            receiver = new FrameReceiver(receiverConfig);
            transmitter = new FrameTransmitter(transmitterConfig);
        } catch (ModemException e) {
            fail("could not set up receiver/transmitter");
        }

        String payload = "Hello, World!";
        try {
            transmitter.send(payload.getBytes());
        } catch (IOException e) {
            fail("error sending on transmitter");
        }

        receiver.setBlocking(1, 0);
        byte[] buf = new byte[80];
        long recvLen = 0;
        recvLen = receiver.receive(buf);

        byte[] recvBuf = Arrays.copyOfRange(buf, 0, (int) recvLen);
        String recvStr;
        recvStr = new String(recvBuf, StandardCharsets.UTF_8);
        assertEquals(recvStr, payload);

        transmitter.close();
        receiver.close();
    }

}