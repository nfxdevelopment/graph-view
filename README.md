# Graph View

The Graph View library provides a flexible static or dynamic graph view for android. 
 Features :
* Live data via signal streams
* Multiple signal support
* Linear and Logarithmic axis
* Custom axis figures
* Optional axis text
* Zooming/Panning objects for the background and signal
* User definable colors

<img src="https://cloud.githubusercontent.com/assets/16980993/26276594/24e94334-3d72-11e7-9d8c-9f9026083885.png" height="255">

## Usage

Add this to your module dependencies:
```groovy
    compile 'com.nfx.android:graph-view:0.0.1'
````

Add the view into your layout
```xml
    <com.nfx.android.graph.androidgraph.GraphManager
        android:id="@+id/graph_manager"/>
````

Create a signal and a listener to feed the data into
```java
InputListener inputListener = graphManager.getSignalManagerInterface().addSignal(
        signalId, sizeOfData, new AxisParameters(0,1, Scale.linear), signalColor);
````

Create a new input signal extending Input
```java
class TestInput extends Input {
    @Override
    public void initialise() { /* Initialisation code */ }

    @Override
    public void start() { /* start signal capture */ }

    @Override
    public void stop() { /* stop signal capture */ }

    @Override
    public void destroy() { /* clean up */ }

    @Override
    public int getSampleRate() { /* return sample rate of signal }

    @Override
    public void setSampleRate(int sampleRate) throws Exception { /* set sample rate }

    @Override
    public int getBufferSize() { /* Get signal buffer size */}

    @Override
    public void setBufferSize(int bufferSize) { /* Set signal buffer size*/ }

    @Override
    public boolean hasTriggerDetection() { /* Are triggers used with this signal */ }

    @Nullable
    @Override
    public TriggerDetection getTriggerDetection() { /* Get Trigger Object */ }
}
````

Add the listener to the signal
```java
testInput.addInputListener(inputListener);
````

Initialise and start the graph and signal implementation 
```java
    graphManager.getGraphViewInterface().start();
    testInput.initialise();
    testInput.start();
````

Stop and destroy graph and signal input
```java
    testInput.stop();
    graphManager.getGraphViewInterface().stop();
    testInput.destroy();
````


## Versioning

[SemVer](http://semver.org/) is used for versioning. For the versions available, see the [tags on this repository](https://github.com/nfxdevelopment/graph-view/tags).

## Authors

* **Nicholas Winder** - *Full implementation* - [nfxdevelopment](https://github.com/nfxdevelopment)

## Licence
Licenced under *Apache2 licence*, so you can do whatever you want with it.
Please push back changes to help the library mature.

## Projects Featuring Graph view
[NFX Specscope](https://play.google.com/store/apps/details?id=com.nfx.android.specscope)

<img src="https://cloud.githubusercontent.com/assets/16980993/26276800/7bc5809c-3d76-11e7-85e5-baac303c61e8.png" height="255">

