(
SuperDirt.postBadValues = false;

// s.options.device_("JackRouter");
// s.options.device_("BlackHole 16ch");


s.options.numBuffers = 1024 * 16;
s.options.memSize = 8192 * 16;
s.options.maxNodes = 1024 * 64;
s.options.numOutputBusChannels = 2;
s.options.numInputBusChannels = 0;

s.waitForBoot {
	~dirt = SuperDirt(2, s);

	// ~dirt.loadSoundFiles("~/studio/moon/kits/*");
	// ~dirt.loadSoundFiles("~/studio/_deleted/moon/long/*");
	// ~dirt.loadSoundFiles("~/studio/_deleted/moon/other/*");
	// ~dirt.loadSoundFiles("~/studio/sample-maker/*");

	// ~dirt.loadSoundFiles("~/studio/tidal-samples/*");

	s.sync;
	~dirt.start(57120, [0]);

	MIDIClient.init;

	// // ~rytmOut = MIDIOut.newByName("Elektron Analog Rytm", "Elektron Analog Rytm");
	// ~rytmOut = MIDIOut.newByName("Elektron Analog Rytm MKII", "Elektron Analog Rytm MKII");
	// ~rytmOut.latency = 0;
	// ~dirt.soundLibrary.addMIDI(\rytm, ~rytmOut);

	~harmorOut = MIDIOut.newByName("IAC Driver", "Bus 1");
	~harmorOut.latency = 0;
	~dirt.soundLibrary.addMIDI(\harmor, ~harmorOut);

	// ~euroOut = MIDIOut.newByName("MIDI 1U", "MIDI 1U");
	// ~euroOut.latency = 0;
	// ~dirt.soundLibrary.addMIDI(\euro, ~euroOut);


};
s.latency = 0;
);


(
// var addr = NetAddr.new("10.0.0.244", 5150);
var addr = NetAddr.new("127.0.0.1", 5150);
OSCFunc({ |msg, time, tidalAddr|
    var latency = time - Main.elapsedTime;
    msg = msg ++ ["time", time, "latency", latency];
	// msg.postln;
    addr.sendBundle(latency, msg);
}, '/dirt/play').fix;
)



// Evaluate the block below to start the mapping MIDI -> OSC.
(
var on, off, cc;
var osc;

osc = NetAddr.new("127.0.0.1", 6010);

MIDIClient.init;
//MIDIIn.connectAll;

MIDIIn.connect(inport: 0, device: 2);

// on = MIDIFunc.noteOn({ |val, num, chan, src|
// 	osc.sendMsg("/ctrl", num.asString, val/127);
// });

// off = MIDIFunc.noteOff({ |val, num, chan, src|
// 	osc.sendMsg("/ctrl", num.asString, 0);
// });

cc = MIDIFunc.cc({ |val, num, chan, src|
	osc.sendMsg("/ctrl", num.asString, val/127);
	val.postln;
});

if (~stopMidiToOsc != nil, {
	~stopMidiToOsc.value;
});

~stopMidiToOsc = {
	on.free;
	off.free;
	cc.free;
};
)

// Evaluate the line below to stop it.
~stopMidiToOsc.value;



~dirt.loadSoundFiles("~/studio/sample-maker/*");

SuperDirt.start