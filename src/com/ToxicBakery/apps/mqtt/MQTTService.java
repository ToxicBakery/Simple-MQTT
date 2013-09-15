package com.ToxicBakery.apps.mqtt;

import java.util.Random;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.internal.MemoryPersistence;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Vibrator;
import android.widget.Toast;

public class MQTTService extends Service implements MqttCallback {

	private static final String SERVER = "tcp://192.168.50.155:1883";
	private static final String TOPIC = "test/123";

	private MqttClient mClient;

	public MQTTService() {
		try {
			final String clientID = String.valueOf(new Random(System
					.currentTimeMillis()).nextInt());
			mClient = new MqttClient(SERVER, clientID, new MemoryPersistence());
			mClient.setCallback(this);
		} catch (MqttException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(getApplicationContext(),
				"Service start command received!", Toast.LENGTH_SHORT).show();

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					if (!mClient.isConnected())
						mClient.connect();

					mClient.subscribe(TOPIC);

					System.out.println("Client connected and subscribed.");

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

		return START_STICKY;
	}

	@Override
	public void connectionLost(Throwable throwable) {
		System.err.println("Connection lost!");
	}

	@Override
	public void deliveryComplete(MqttDeliveryToken topic) {
		System.out.println("Delivery Complete: " + topic);
	}

	@Override
	public void messageArrived(MqttTopic topic, MqttMessage msg)
			throws Exception {
		System.out.println("Message Arrived: " + topic + " - " + msg);
		final Vibrator vibrator = (Vibrator) getApplicationContext()
				.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(500);
	}

}
