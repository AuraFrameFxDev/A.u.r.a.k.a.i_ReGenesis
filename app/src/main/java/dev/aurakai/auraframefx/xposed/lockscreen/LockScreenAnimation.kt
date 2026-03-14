package dev.aurakai.auraframefx.xposed.lockscreen

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog

/**
 * LockScreen Animation Hooker
 *
 * Hooks SystemUI's keyguard/lockscreen layer to inject custom
 * animations and Aura visual effects on lock/unlock transitions.
 *
 * Status: stub — gated behind FeatureToggles.XPOSED_LOCKSCREEN_HOOKS (false).
 * Wire in GenesisHookEntry.onHook() once animations are implemented.
 */
class LockScreenAnimationHooker : YukiBaseHooker() {

    override fun onHook() {
        // ── Keyguard visibility change ────────────────────────────────────────
        "com.android.systemui.keyguard.KeyguardViewMediator".toClassOrNull()?.apply {
            method {
                name = "onKeyguardExitFinished"
            }.hook {
                after {
                    YLog.info("LockScreen-Hook: Keyguard exited — play unlock animation")
                    playUnlockAnimation()
                }
            }

            method {
                name = "doKeyguardLocked"
            }.hook {
                after {
                    YLog.info("LockScreen-Hook: Keyguard locked — play lock animation")
                    playLockAnimation()
                }
            }
        }

        // ── Bouncer (PIN/pattern entry) ───────────────────────────────────────
        "com.android.systemui.keyguard.ui.viewmodel.KeyguardBouncerViewModel"
            .toClassOrNull()?.apply {
                method {
                    name = "onBouncerShowing"
                }.hook {
                    after {
                        YLog.info("LockScreen-Hook: Bouncer shown — apply Aura theme")
                        applyBouncerTheme()
                    }
                }
            }
    }

    private fun playUnlockAnimation() {
        // TODO: trigger Aura particle burst on unlock
    }

    private fun playLockAnimation() {
        // TODO: fade-in Aura lock overlay
    }

    private fun applyBouncerTheme() {
        // TODO: apply Kai Sentinel color scheme to PIN/pattern bouncer
    }
}
