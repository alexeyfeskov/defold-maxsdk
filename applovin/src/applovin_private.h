#pragma once

namespace dmAppLovinMax {

void Initialize_Ext();

void Initialize();
void SetMuted(bool muted);
void SetVerboseLogging(bool verbose);
void SetHasUserConsent(bool hasConsent);
void SetIsAgeRestrictedUser(bool restricted);
void SetDoNotSell(bool doNotSell);

void LoadInterstitial(const char* unitId);
void ShowInterstitial();

} //namespace dmAppLovinMax
