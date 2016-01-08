/*
 * libjingle
 * Copyright 2013 Google Inc.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

// --------------------
// NOTE: All enums are changed from `typdef enum` to `typedef NS_ENUM` for interoperability with swift
#import <Foundation/Foundation.h>
// --------------------
// Enums that are common to the ObjectiveC version of the PeerConnection API.

// RTCICEConnectionState correspond to the states in webrtc::ICEConnectionState.
typedef NS_ENUM(NSInteger, RTCICEConnectionState) {
  RTCICEConnectionNew,
  RTCICEConnectionChecking,
  RTCICEConnectionConnected,
  RTCICEConnectionCompleted,
  RTCICEConnectionFailed,
  RTCICEConnectionDisconnected,
  RTCICEConnectionClosed,
  RTCICEConnectionMax,
};

// RTCICEGatheringState the states in webrtc::ICEGatheringState.
typedef NS_ENUM(NSInteger, RTCICEGatheringState) {
  RTCICEGatheringNew,
  RTCICEGatheringGathering,
  RTCICEGatheringComplete,
};

// RTCSignalingState correspond to the states in webrtc::SignalingState.
typedef NS_ENUM(NSInteger, RTCSignalingState) {
  RTCSignalingStable,
  RTCSignalingHaveLocalOffer,
  RTCSignalingHaveLocalPrAnswer,
  RTCSignalingHaveRemoteOffer,
  RTCSignalingHaveRemotePrAnswer,
  RTCSignalingClosed,
};

// RTCStatsOutputLevel correspond to webrtc::StatsOutputLevel
typedef NS_ENUM(NSInteger, RTCStatsOutputLevel) {
  RTCStatsOutputLevelStandard,
  RTCStatsOutputLevelDebug,
};

// RTCSourceState corresponds to the states in webrtc::SourceState.
typedef NS_ENUM(NSInteger, RTCSourceState) {
  RTCSourceStateInitializing,
  RTCSourceStateLive,
  RTCSourceStateEnded,
  RTCSourceStateMuted,
};

// RTCTrackState corresponds to the states in webrtc::TrackState.
typedef NS_ENUM(NSInteger, RTCTrackState) {
  RTCTrackStateInitializing,
  RTCTrackStateLive,
  RTCTrackStateEnded,
  RTCTrackStateFailed,
};