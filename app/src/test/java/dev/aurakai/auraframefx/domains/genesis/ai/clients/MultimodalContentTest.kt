package dev.aurakai.auraframefx.domains.genesis.ai.clients

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 *
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("MultimodalContent and MrlDimension Tests")
class MultimodalContentTest {


    @Nested
    @DisplayName("MultimodalContent.Text")

        @Test
        }

        @Test
        }

        @Test
        }

        @Test
        }

        @Test
        }
    }


    @Nested
    @DisplayName("MultimodalContent.Image")


        @Test
        }

        @Test
        }

        @Test
        }

        @Test
        }
    }


    @Nested
    @DisplayName("MultimodalContent.Audio")



        @Test
        }

        @Test
        }

        @Test
        }
    }


    @Nested

        @Test
                MultimodalContent.Text("t"),
                MultimodalContent.Image("i"),
            )
                when (content) {
                    is MultimodalContent.Text -> "text"
                    is MultimodalContent.Image -> "image"
                    is MultimodalContent.Audio -> "audio"
                }
            }
            assertEquals(listOf("text", "image", "audio"), tags)
        }

        }
    }


    @Nested

        @Test
            assertEquals(768, MrlDimension.FAST)
        }

        @Test
            assertEquals(1536, MrlDimension.OPTIMAL)
        }

        @Test
            assertEquals(3072, MrlDimension.DEEP)
        }

        @Test
        }

        @Test
        }
    }
}