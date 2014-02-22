/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\yangxj\\Desktop\\≤‚ ‘”√¥˙¬Î\\aidl App_elfPlayer\\App_elfPlayer\\src\\app\\android\\simpleplayer\\IServicePlayer.aidl
 */
package app.android.simpleplayer;
public interface IServicePlayer extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements app.android.simpleplayer.IServicePlayer
{
private static final java.lang.String DESCRIPTOR = "app.android.simpleplayer.IServicePlayer";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an app.android.simpleplayer.IServicePlayer interface,
 * generating a proxy if needed.
 */
public static app.android.simpleplayer.IServicePlayer asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof app.android.simpleplayer.IServicePlayer))) {
return ((app.android.simpleplayer.IServicePlayer)iin);
}
return new app.android.simpleplayer.IServicePlayer.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_play:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
this.play(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_pause:
{
data.enforceInterface(DESCRIPTOR);
this.pause();
reply.writeNoException();
return true;
}
case TRANSACTION_stop:
{
data.enforceInterface(DESCRIPTOR);
this.stop();
reply.writeNoException();
return true;
}
case TRANSACTION_changeTo:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.changeTo(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getDuration:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getDuration();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getCurrentPosition:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
int _result = this.getCurrentPosition(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_seekTo:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.seekTo(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_isPlaying:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isPlaying();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_isCompleted:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isCompleted();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_release:
{
data.enforceInterface(DESCRIPTOR);
this.release();
reply.writeNoException();
return true;
}
case TRANSACTION_reset:
{
data.enforceInterface(DESCRIPTOR);
this.reset();
reply.writeNoException();
return true;
}
case TRANSACTION_setLoop:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
java.lang.String _arg1;
_arg1 = data.readString();
boolean _result = this.setLoop(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements app.android.simpleplayer.IServicePlayer
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public void play(int progress, java.lang.String name) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(progress);
_data.writeString(name);
mRemote.transact(Stub.TRANSACTION_play, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void pause() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_pause, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void stop() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stop, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void changeTo(java.lang.String name) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(name);
mRemote.transact(Stub.TRANSACTION_changeTo, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public int getDuration() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDuration, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getCurrentPosition(boolean playing) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((playing)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_getCurrentPosition, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void seekTo(int current) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(current);
mRemote.transact(Stub.TRANSACTION_seekTo, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean isPlaying() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isPlaying, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean isCompleted() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isCompleted, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void release() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_release, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void reset() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_reset, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean setLoop(boolean loop, java.lang.String name) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((loop)?(1):(0)));
_data.writeString(name);
mRemote.transact(Stub.TRANSACTION_setLoop, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_play = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_pause = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_stop = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_changeTo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_getDuration = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_getCurrentPosition = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_seekTo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_isPlaying = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_isCompleted = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_release = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_reset = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_setLoop = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
}
public void play(int progress, java.lang.String name) throws android.os.RemoteException;
public void pause() throws android.os.RemoteException;
public void stop() throws android.os.RemoteException;
public void changeTo(java.lang.String name) throws android.os.RemoteException;
public int getDuration() throws android.os.RemoteException;
public int getCurrentPosition(boolean playing) throws android.os.RemoteException;
public void seekTo(int current) throws android.os.RemoteException;
public boolean isPlaying() throws android.os.RemoteException;
public boolean isCompleted() throws android.os.RemoteException;
public void release() throws android.os.RemoteException;
public void reset() throws android.os.RemoteException;
public boolean setLoop(boolean loop, java.lang.String name) throws android.os.RemoteException;
}
