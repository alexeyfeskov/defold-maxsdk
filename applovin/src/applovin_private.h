#pragma once

namespace dmAppLovinMax {

void Initialize_Ext();

void Initialize();
void SetMuted(bool muted);
void SetVerboseLogging(bool verbose);
void SetHasUserConsent(bool hasConsent);
void SetIsAgeRestrictedUser(bool ageRestricted);
void SetDoNotSell(bool doNotSell);

void LoadInterstitial(const char* unitId);
void ShowInterstitial();
bool IsInterstitialLoaded();

void LoadRewarded(const char* unitId);
void ShowRewarded();
bool IsRewardedLoaded();

} //namespace dmAppLovinMax
